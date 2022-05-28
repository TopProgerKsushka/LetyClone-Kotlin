package org.ksushka.letyclone.model.bse

import javax.persistence.*

@Entity
@Table(name = "bse_order")
data class Order (
    val letycloneUserId: Long? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val items: List<OrderedItem> = listOf()
}