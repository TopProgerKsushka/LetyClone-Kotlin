package org.ksushka.letyclone

import org.ksushka.letyclone.model.letyclone.Item
import org.ksushka.letyclone.model.letyclone.Shop
import org.ksushka.letyclone.model.letyclone.User
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.persistence.EntityManager
import javax.persistence.Persistence
import javax.servlet.http.HttpSession

fun fillModel(em: EntityManager, s: HttpSession, m: ModelMap) {
	val userId = s.getAttribute("user_id") as Long?
	if (userId != null) {
		val u = em.find(User::class.java, userId)
		if (u != null) {
			m["user_id"] = userId
			m["username"] = u.userName

			m["actualBalance"] = u.balance

			val q = em.createQuery("select i from Item i where i.user = :user and i.canBeRefunded = true and i.refunded = false", Item::class.java)
			q.setParameter("user", u)
			val items = q.resultList

			var ghostBalance = 0.0
			for (item in items) {
				ghostBalance += item.cashback
			}

			m["ghostBalance"] = ghostBalance

		} else {
			m["user_id"] = null
			m["username"] = null
		}
	} else {
		m["user_id"] = null
		m["username"] = null
	}

	val shops = em.createQuery("select s from Shop s", Shop::class.java).resultList
	m["shops"] = shops
}

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@RestController
class LetyCloneApplication {
	private val emf = Persistence.createEntityManagerFactory("org.ksushka.letyclone")

	@GetMapping("/", "index", "index.html")
	fun index(s: HttpSession, m: ModelMap): ModelAndView {
		val em = emf.createEntityManager()

		fillModel(em, s, m)
		return ModelAndView("index", m)
	}

	@GetMapping("/balance", "balance.html")
	fun balance(s: HttpSession, m: ModelMap): ModelAndView {
		val em = emf.createEntityManager()

		fillModel(em, s, m)

		return if (s.getAttribute("user_id") as Long? != null)
			ModelAndView("balance", m)
		else
			ModelAndView(RedirectView("/"))
	}
}

fun main(args: Array<String>) {
	runApplication<LetyCloneApplication>(*args)
}
