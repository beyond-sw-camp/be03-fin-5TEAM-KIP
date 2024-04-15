<script setup>
const productCounts = reactive({});

// 피니아.
const cart = useCart()
const color = useColor();
const product = useProduct()

const addItems = (product) => {
  cart.addItems(productCounts[product.name], product);
  productCounts[product.name] = 0;
}

</script>

<template>
  <v-container class="pina__container">
    <v-sheet class="d-flex ma-auto">
      <v-row>
        <v-col
            v-for="product in product.getProducts"
            :key="product.name"
            cols="12"
            sm="6"
            md="4"
            lg="3"
            xl="6"
        >
          <v-card
              max-width="450"
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
                  :color="color.kipMainColor"
                  variant="elevated"
              />
            </v-card-actions>

          </v-card>
        </v-col>
      </v-row>
    </v-sheet>
  </v-container>
</template>
<style>
.pina__container{
  height: 90vh;
  display: flex;
}
</style>