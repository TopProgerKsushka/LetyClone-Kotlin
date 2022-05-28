package org.ksushka.letyclone

import org.ksushka.letyclone.model.letyclone.User
import org.springframework.http.MediaType
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.persistence.Persistence
import javax.servlet.http.HttpSession

data class RegisterLoginForm(
    val email: String,
    val password: String,
)

@RestController
@RequestMapping("/user_api")
class UserApi {
    val emf = Persistence.createEntityManagerFactory("org.ksushka.letyclone")

    @PostMapping("register", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun register(s: HttpSession, form: RegisterLoginForm): ModelAndView {
        val em = emf.createEntityManager()

        if (!Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]+$").matches(form.email)) {
            val m = ModelMap()
            fillModel(em, s, m);
            m["message"] = "Некорректный email"
            return ModelAndView("index", m)
        }
        val q = em.createQuery("select u from User u where u.email = :email")
        q.setParameter("email", form.email)
        val users = q.resultList
        if (!users.isEmpty()) {
            val m = ModelMap()
            fillModel(em, s, m);
            m["message"] = "Пользователь с таким email уже зарегистрирован"
            return ModelAndView("index", m)
        }
        if (!Regex("^\\w+$").matches(form.password)) {
            val m = ModelMap()
            fillModel(em, s, m);
            m["message"] = "Пароль не должен быть пустым, он может содержать буквы, цифры и символ подчёркивания"
            return ModelAndView("index", m)
        }

        val u = User(form.email, form.password)
        em.transaction.begin()
        em.persist(u)
        em.transaction.commit()
        s.setAttribute("user_id", u.id)
        return ModelAndView(RedirectView("/"))
    }

    @PostMapping("login", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun login(s: HttpSession, form: RegisterLoginForm): ModelAndView {
        val em = emf.createEntityManager()

        val q = em.createQuery("select u from User u where u.email = :email", User::class.java)
        q.setParameter("email", form.email)
        val users = q.resultList
        if (users.isEmpty()) {
            val m = ModelMap()
            fillModel(em, s, m);
            m["message"] = "Пользователь с таким email не зарегистрирован"
            return ModelAndView("index", m)
        }
        val user = users[0]
        if (user.password != form.password) {
            val m = ModelMap()
            fillModel(em, s, m);
            m["message"] = "Неверный пароль"
            return ModelAndView("index", m)
        }
        s.setAttribute("user_id", user.id)
        return ModelAndView(RedirectView("/"))
    }

    @GetMapping("logout")
    fun logout(s: HttpSession): ModelAndView {
        s.removeAttribute("user_id")
        return ModelAndView(RedirectView("/"))
    }

    @PostMapping("withdraw")
    fun withdraw(s: HttpSession): ModelAndView {
        val em = emf.createEntityManager()
        val userId = s.getAttribute("user_id")
        val user = em.find(User::class.java, userId)
        em.transaction.begin()
        user.balance = 0.0
        em.transaction.commit()
        return ModelAndView(RedirectView("/balance"))
    }
}