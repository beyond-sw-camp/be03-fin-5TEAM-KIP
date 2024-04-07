<script setup>
import CartStore from "@/stores/CartStore";
import AuthUserStore from "~/stores/AuthUserStore.js";
import ProductStore from "@/stores/ProductStore";
import KipColor from "~/stores/KipColor.js";

ProductStore().fill()

const productCounts = reactive({});
const addItems = (product) => {
  CartStore().addItems(productCounts[product.name], product);
  productCounts[product.name] = 0;
}

</script>

<template>
  <v-sheet
      class="d-flex pa-10"
      :color="KipColor().kipMainColor"
  >
    <v-row>
      <v-col
          v-for="product in ProductStore().getProducts"
          :key="product.name"
          :cols="12"
          sm="6"
          md="4"
          lg="3"
      >
        <v-card
            class="mx-auto"
            max-width="400"
        >
          <v-img
              class="align-end text-white"
              height="200"
              :src="`/images/${product.image}`"
              cover
          />
          <v-card-title style="color:black">{{ product.name }}</v-card-title>
          <v-card-subtitle> ${{ product.price }}</v-card-subtitle>

          <v-text-field
              v-model="productCounts[product.name]"
              type="number"
              label="Count"
              class="mt-4"
              min="1"
          />

          <v-card-actions>
            <v-spacer/>
            <v-btn
                text="Add To Cart"
                @click="addItems(product)"
                :color="KipColor().kipMainColor"
                variant="elevated"
            />
          </v-card-actions>

        </v-card>
      </v-col>
    </v-row>
  </v-sheet>
</template>
