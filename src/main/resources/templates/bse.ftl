<#import "/spring.ftl" as spring />
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Best Shop Ever</title>
    <link rel="stylesheet" href="/bse.css" />
</head>
<body>
<div class="header">
    <div>
        <a href="/bse"><img height="100" src="/img/bse_logo.svg"/></a>
    </div>
    <div class="header-client"></div>
    <div>
        <a href="/bse/orders">Все заказы</a>
    </div>
</div>
<div class="client">
    <div class="product-list">
        <div>
            <span class="frame-title">Каталог</span>
        </div>
        <div>
            <#list products as product>
                <div class="product">
                    <img src="${product.pictureUrl}" class="product-picture" />
                    <div class="product-client">
                        <p><span class="product-name">${product.name}</span></p>
                        <p><b>Цена:</b> ₽ ${product.price}</p>
                        <#if lc_user_id?? && product.cashbackPercent??>
                            <p><b>Кэшбэк:</b> ${product.cashbackPercent}%</p>
                            <p><b>Кэшбэк в рублях:</b> ₽ ${product.price * (product.cashbackPercent / 100)}</p>
                        </#if>
                    </div>
                    <div><input type="button" value="Добавить в корзину" onclick="addToCart(${product.id});" /></div>
                </div>
            </#list>
        </div>
    </div>
    <div class="cart-block">
        <div>
            <span class="frame-title">Корзина</span>
            <div id="cart">
            </div>
            <div class="cart-summary">
                <p><span class="sum">Сумма: ₽ <span id="sum">0,00</span></span></p>
                <#if lc_user_id??>
                    <p><span class="cashback">Кэшбэк: ₽ <span id="cashback">0,00</span></span></p>
                </#if>
            </div>
            <form class="cart-form" method="POST" action="/bse/create_order">
                <#if lc_user_id??>
                    <input type="hidden" name="lc_user_id" value="${lc_user_id}">
                </#if>
                <input type="hidden" name="products" value="" />
                <input type="submit" id="order-submit" value="Осуществить заказ" disabled>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript">

    const products = {
        <#list products as product>
            ${product.id}: {
                "name": "${product.name}",
                "pic": "${product.pictureUrl}",
                "price": ${product.price?c},
                <#if lc_user_id??>
                "cashback": ${product.cashbackPercent?c},
                </#if>
            },
        </#list>
    };

    function addToCart(id) {
        const cart = document.getElementById("cart");
        const count = cart.children.length;
        const cart_product = document.createElement("div");
        cart_product.className = "cart-product";
        const cart_product_picture = document.createElement("img");
        cart_product_picture.src = products[id].pic;
        cart_product_picture.className = "cart-product-picture";
        const cart_product_client = document.createElement("div");
        cart_product_client.className = "cart-product-client";
        cart_product_client.innerText = products[id].name;
        const div = document.createElement("div");
        const delButton = document.createElement("input");
        delButton.type = "button";
        delButton.value = "Удалить";
        div.append(delButton);
        cart_product.append(cart_product_picture);
        cart_product.append(cart_product_client);
        cart_product.append(div);

        const h_id = document.createElement("input");
        h_id.type = "hidden";
        h_id.id = "h-id";
        h_id.value = id;

        const h_price = document.createElement("input");
        h_price.type = "hidden";
        h_price.id = "h-price";
        h_price.value = products[id].price;

        <#if lc_user_id??>
        const h_cashback = document.createElement("input");
        h_cashback.type = "hidden";
        h_cashback.id = "h-cashback";
        h_cashback.value = products[id].cashback;
        </#if>

        cart_product.append(h_id);
        cart_product.append(h_price);
        <#if lc_user_id??>
        cart_product.append(h_cashback);
        </#if>

        delButton.onclick = function () {
            removeFromCart(cart_product);
        }

        cart.append(cart_product);

        recalculateCart();
    }

    function removeFromCart(item) {
        const cart = document.getElementById("cart");
        cart.removeChild(item);

        recalculateCart();
    }

    function recalculateCart() {
        const cart = document.getElementById("cart");
        let sum = 0;
        let ids = "";
        <#if lc_user_id??>let cashbackSum = 0;</#if>
        for (const child of cart.children) {
            const id = child.querySelector("#h-id").value;
            if (!ids) {
                ids = id;
            } else {
                ids += "," + id;
            }
            const price = parseFloat(child.querySelector("#h-price").value);
            <#if lc_user_id??>
            const cashback = parseFloat(child.querySelector("#h-cashback").value);
            </#if>
            sum += price;
            <#if lc_user_id??>
            cashbackSum += price * (cashback / 100);
            </#if>
        }
        const sumEl = document.getElementById("sum");
        sumEl.innerText = formatFloat(sum.toFixed(2));
        <#if lc_user_id??>
        const cashbackEl = document.getElementById("cashback");
        cashbackEl.innerText = formatFloat(cashbackSum.toFixed(2));
        </#if>

        const h_products = document.querySelector("input[type=hidden][name='products']");
        h_products.value = ids;

        const submit = document.getElementById("order-submit");
        if (cart.children.length > 0) {
            submit.disabled = false;
        } else {
            submit.disabled = true;
        }
    }

    function formatFloat(n) {
        const s = n.toString().split(".");
        let intPart = "";
        let c = 0;
        for (let i = s[0].length - 1; i >= 0; --i) {
            if (c > 2) {
                intPart = s[0].charAt(i) + " " + intPart;
                c = 1;
            } else {
                intPart = s[0].charAt(i) + intPart;
                ++c;
            }
        }
        return intPart + "," + s[1];
    }
</script>
</body>
</html>