<script setup>
import AuthUserStore from "~/stores/AuthUserStore";
import CartStore from "~/stores/CartStore";
import KipColor from "~/stores/KipColor";


</script>

<template>
  <v-card :title="`${AuthUserStore().name}'s Notification`"
          class="mx-auto"
          max-width="450">
    <v-card
        v-for="product in CartStore().grouped"
        class="mx-auto"
        max-width="344"
        :subtitle="`$${product[0].price} X ${CartStore().groupCount(product[0].name)}개`"
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
            @click="CartStore().deleteItems(product[0].name)"
        />
      </template>

    </v-card>

    <!--닫기버튼-->
    <v-card-actions>
      Total Price: ${{ CartStore().totalPrice }}
      <v-spacer/>
      <v-btn
          text="X"
          @click="$emit('isActive')"
          :color="KipColor().kipMainColor"
          variant="flat"
      />
    </v-card-actions>
  </v-card>
</template>