/** order_cart : totalPriceCard : 총 구매 금액 calculate **/
/** order_cart : totalPriceCard : 총 구매 금액 calculate **/
$(function () {
  const finalPrice = parseFloat(document.getElementById('finalPrice').value);
  let totalPrice = finalPrice;

  // 구매수량 - 버튼
  $(".minus").on("click", function (e) {
      e.preventDefault();
      let $input = $(this).next("input");
      let minValue = parseInt($input.attr("min"));
      if (parseInt($input.val()) > minValue) {
          $input.val(parseInt($input.val()) - 1);
      }
      updateTotalPrice(finalPrice, $input.val());
      console.log(totalPrice);
  });

  // 구매수량 + 버튼
  $(".plus").on("click", function (e) {
      e.preventDefault();
      let $input = $(this).prev("input");
      let maxValue = parseInt($input.attr("max"));
      if (parseInt($input.val()) < maxValue) {
          $input.val(parseInt($input.val()) + 1);
      }
      updateTotalPrice(finalPrice, $input.val());
  });

  function updateTotalPrice(finalPrice, quantity) {
      totalPrice = finalPrice * quantity;
      $(".prodTotalPrice").text(totalPrice.toLocaleString());
  }
}); // $(function(){}); jQuery END
/** order_cart : totalPriceCard : 총 구매 금액 calculate **/
/** order_cart : totalPriceCard : 총 구매 금액 calculate **/
