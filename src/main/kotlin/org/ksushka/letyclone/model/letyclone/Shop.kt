package org.ksushka.letyclone.model.letyclone

import javax.persistence.*

@Entity
@Table(name = "lc_shop")
data class Shop(
    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false, unique = true)
    val url: String,

    @Column(nullable = false)
    val logoUrl: String,

    @Column(nullable = false)
    val maxCashback: Double,

    val description: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}