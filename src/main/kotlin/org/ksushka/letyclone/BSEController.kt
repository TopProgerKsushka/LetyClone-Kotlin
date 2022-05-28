package org.ksushka.letyclone

import com.google.gson.Gson
import org.ksushka.letyclone.model.bse.Order
import org.ksushka.letyclone.model.bse.OrderedItem
import org.ksushka.letyclone.model.bse.Product
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.persistence.Persistence
import javax.servlet.http.HttpServletRequest
import kotlin.math.roundToInt

@RestController
@RequestMapping("/bse")
class BSEController {
    private val emf = Persistence.createEntityManagerFactory("org.ksushka.letyclone")

    @GetMapping("", "/index", "/index.html")
    fun bse(m: ModelMap, @RequestParam(name = "lc_user_id") lcUserId: Long?): ModelAndView {
        val em = emf.createEntityManager()

        m["lc_user_id"] = lcUserId

        val products = em.createQuery("select p from Product p", Product::class.java).resultList
        m["products"] = products

        return ModelAndView("bse", m)
    }

    @PostMapping("create_order", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun createOrder(
        req: HttpServletRequest,
        @RequestParam(name = "lc_user_id") letycloneUserId: Long?,
        @RequestParam(name = "products") ids: String
    ): ModelAndView {
        val em = emf.createEntityManager()

        val products = ids.split(",").map { em.find(Product::class.java, it.toLong()) }
        em.transaction.begin()
        val order = Order(letycloneUserId)
        em.persist(order)
        val items = mutableListOf<OrderedItem>()
        for (product in products) {
            val item = OrderedItem(order, product)
            items.add(item)
            em.persist(item)
        }
        em.transaction.commit()
        if (letycloneUserId != null) {
            val rt = RestTemplate()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val json = object {
                val shopId = 1
                val userId = letycloneUserId
                val items = items.map {
                    val cashback = (it.product.price * it.product.cashbackPercent).roundToInt() / 100.0
                    object {
                        val itemId = it.id
                        val cashback = cashback
                        val canBeRefunded = it.canBeRefunded
                        val refunded = it.refunded
                    }
                }
            }

            val gson = Gson()
            val host = Regex("^https?://.+?(/|$)").find(req.requestURL.toString())?.value ?: "http://localhost:8080/"
            rt.postForObject(
                "${host}partner_api/user_created_order",
                HttpEntity<String>(gson.toJson(json), headers),
                String::class.java
            )
        }
        return ModelAndView(RedirectView("/bse/order?id=${order.id}"))
    }

    @GetMapping("order", "order.html")
    fun order(@RequestParam id: Long?, m: ModelMap): ModelAndView {
        val em = emf.createEntityManager()

        val order = em.find(Order::class.java, id)

        m["order"] = order
        return ModelAndView("bse_order", m)
    }

    @GetMapping("orders", "orders.html")
    fun orders(m: ModelMap): ModelAndView {
        val em = emf.createEntityManager()

        val orders = em.createQuery("select o from Order o", Order::class.java).resultList

        m["orders"] = orders
        return ModelAndView("bse_orders", m)
    }

    @PostMapping("refund", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun refund(
        req: HttpServletRequest,
        @RequestParam(name = "lc_user_id") letycloneUserId: Long?,
        @RequestParam(name = "item_id") itemId: Long
    ): ModelAndView {
        val em = emf.createEntityManager()

        val item = em.find(OrderedItem::class.java, itemId)
        if (!item.canBeRefunded) {
            return ModelAndView(RedirectView("/bse/order?id=${item.order.id}"))
        }

        em.transaction.begin()
        item.refunded = true
        em.transaction.commit()

        if (letycloneUserId != null) {
            val rt = RestTemplate()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val json = object {
                val shopId = 1
                val userId = letycloneUserId
                val itemId = itemId
            }

            val gson = Gson()
            val host = Regex("^https?://.+?(/|$)").find(req.requestURL.toString())?.value ?: "http://localhost:8080/"
            rt.postForObject(
                "${host}partner_api/item_refunded",
                HttpEntity<String>(gson.toJson(json), headers),
                String::class.java
            )
        }

        return ModelAndView(RedirectView("/bse/order?id=${item.order.id}"))
    }

    @PostMapping("nonrefundable", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun nonrefundable(
        req: HttpServletRequest,
        @RequestParam(name = "order_id") orderId: Long
    ): ModelAndView {
        val em = emf.createEntityManager()

        val order = em.find(Order::class.java, orderId)

        val ids = mutableListOf<Long>()

        em.transaction.begin()
        for (item in order.items) {
            item.canBeRefunded = false
            ids.add(item.id!!)
        }
        em.transaction.commit()

        if (order.letycloneUserId != null) {
            val rt = RestTemplate()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val json = object {
                val shopId = 1
                val userId = order.letycloneUserId
                val itemIds = ids
            }

            val gson = Gson()
            val host = Regex("^https?://.+?(/|$)").find(req.requestURL.toString())?.value ?: "http://localhost:8080/"
            rt.postForObject(
                "${host}partner_api/items_nonrefundable",
                HttpEntity<String>(gson.toJson(json), headers),
                String::class.java
            )
        }

        return ModelAndView(RedirectView("/bse/order?id=${order.id}"))
    }
}