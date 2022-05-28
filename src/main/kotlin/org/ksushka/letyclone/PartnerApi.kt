package org.ksushka.letyclone

import org.ksushka.letyclone.model.letyclone.Item
import org.ksushka.letyclone.model.letyclone.Shop
import org.ksushka.letyclone.model.letyclone.User
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.persistence.Persistence

data class UserCreatedOrderRequestItem(
    val itemId: Long,
    val cashback: Double,
    val canBeRefunded: Boolean,
    val refunded: Boolean,
)

data class UserCreatedOrderRequest(
    val shopId: Long,
    val userId: Long,
    val items: List<UserCreatedOrderRequestItem>,
)

data class ItemRefundedRequest(
    val shopId: Long,
    val userId: Long,
    val itemId: Long,
)

data class ItemsNonrefundableRequest(
    val shopId: Long,
    val userId: Long,
    val itemIds: List<Long>,
)

@RestController
@RequestMapping("/partner_api")
class PartnerApi {
    private val emf = Persistence.createEntityManagerFactory("org.ksushka.letyclone")

    @PostMapping("user_created_order", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun userCreatedOrder(@RequestBody req: UserCreatedOrderRequest): Any {
        val em = emf.createEntityManager()

        val shop = em.find(Shop::class.java, req.shopId)
        val user = em.find(User::class.java, req.userId)
        em.transaction.begin()
        for (reqItem in req.items) {
            val item = Item(user, shop, reqItem.cashback, reqItem.canBeRefunded, reqItem.refunded, reqItem.itemId)
            em.persist(item)
        }
        em.transaction.commit()

        return object {
            val status = "ok"
        }
    }

    @PostMapping("item_refunded", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun itemRefunded(@RequestBody req: ItemRefundedRequest): Any {
        val em = emf.createEntityManager()

        val q = em.createQuery("select i from Item i where i.user.id = :user_id and i.shop.id = :shop_id and i.shopOrderedItemId = :item_id", Item::class.java)
        q.setParameter("user_id", req.userId)
        q.setParameter("shop_id", req.shopId)
        q.setParameter("item_id", req.itemId)
        val items = q.resultList

        if (items.size != 1) {
            return object {
                val status = "error"
                val message = "Mistake in database"
            }
        }

        if (!items[0].canBeRefunded) {
            return object {
                val status = "error"
                val message = "Item cannot be refunded"
            }
        }

        em.transaction.begin()
        items[0].refunded = true
        em.transaction.commit()

        return object {
            val status = "ok"
        }
    }

    @PostMapping("items_nonrefundable", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun itemsNonrefundable(@RequestBody req: ItemsNonrefundableRequest): Any {
        val em = emf.createEntityManager()

        val user = em.find(User::class.java, req.userId)
        em.transaction.begin()
        for (itemId in req.itemIds) {
            val q = em.createQuery("select i from Item i where i.user = :user and i.shop.id = :shop_id and i.shopOrderedItemId = :item_id", Item::class.java)
            q.setParameter("user", user)
            q.setParameter("shop_id", req.shopId)
            q.setParameter("item_id", itemId)
            val items = q.resultList

            if (items.size != 1) {
                return object {
                    val status = "error"
                    val message = "Mistake in database"
                }
            }

            items[0].canBeRefunded = false

            if (!items[0].refunded) {
                user.balance += items[0].cashback
            }
        }
        em.transaction.commit()

        return object {
            val status = "ok"
        }
    }
}