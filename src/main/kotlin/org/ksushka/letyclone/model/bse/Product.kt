package org.ksushka.letyclone.model.bse

import javax.persistence.*

@Entity
@Table(name = "bse_product")
data class Product(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val price: Double,

    val cashbackPercent: Double,
    val pictureUrl: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}