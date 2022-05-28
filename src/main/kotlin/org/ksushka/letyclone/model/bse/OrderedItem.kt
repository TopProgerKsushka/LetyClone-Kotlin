package org.ksushka.letyclone.model.bse

import javax.persistence.*

@Entity
@Table(name = "bse_ordered_item")
data class OrderedItem(
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    val order: Order,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    val product: Product,

    @Column(nullable = false)
    var canBeRefunded: Boolean = true,

    @Column(nullable = false)
    var refunded: Boolean = false
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}