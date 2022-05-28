package org.ksushka.letyclone.model.letyclone

import javax.persistence.*

@Entity
@Table(name = "lc_item")
data class Item(
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    val shop: Shop,

    @Column(nullable = false)
    val cashback: Double,

    @Column(nullable = false)
    var canBeRefunded: Boolean,

    @Column(nullable = false)
    var refunded: Boolean,

    @Column(nullable = false)
    val shopOrderedItemId: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
