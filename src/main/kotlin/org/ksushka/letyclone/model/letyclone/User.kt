package org.ksushka.letyclone.model.letyclone

import javax.persistence.*

@Entity
@Table(name = "lc_user")
data class User (
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val orderedItems: Set<Item> = setOf()

    @Column(nullable = false)
    var balance: Double = 0.0

    val userName
    get() = email.split("@")[0]
}