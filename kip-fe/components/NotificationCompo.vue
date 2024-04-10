<script setup>
// 피니아
const user = useUser();
const cart = useCart();
const color = useColor()
</script>

<template>
  <v-card :title="`${user.getUserInfo.name}'s Notification`"
          class="mx-auto"
          max-width="450">
    <v-card
        v-for="product in cart.grouped"
        class="mx-auto"
        max-width="344"
        :subtitle="`$${product[0].price} X ${cart.groupCount(product[0].name)}개`"
        :title="product[0].name">

      <template v-slot:prepend>
        <v-avatar
            :image="`/images/${product[0].image}`"
            size="55"
        />
      </template>

      <template v-slot:append>
        <v-icon
            icon="mdi-trash-can"
            size="x-large"
            @click="cart.deleteItems(product[0].name)"
        />
      </template>

    </v-card>

    <!--닫기버튼-->
    <v-card-actions>
      Total Price: ${{ cart.totalPrice }}
      <v-spacer/>
      <v-btn
          text="X"
          @click="$emit('isActive')"
          :color="color.kipMainColor"
          variant="flat"
      />
    </v-card-actions>
  </v-card>
</template>