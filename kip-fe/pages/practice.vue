<template>
  <v-app>
    <!-- Sidebar -->
    <v-navigation-drawer permanent app class="deep-blue">
      <v-list dense class="white--text">
        <v-list-item link to="/introduction">
          <v-list-item-content>
            <v-list-item-title>소개</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-list-item link to="/core-guides">
          <v-list-item-content>
            <v-list-item-title>핵심 가이드</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <!-- Add other sidebar items here -->
      </v-list>
    </v-navigation-drawer>

    <!-- Main content -->
    <v-main>
      <v-container fluid>
        <v-row>

          <!-- Main content left column: Introduction and What is Vue? -->
          <v-col cols="12" md="9">
            <v-card flat class="mb-4">
              <v-card-title class="headline">소개</v-card-title>
              <!-- Introduction section content -->
            </v-card>
            <v-card outlined class="px-5 py-3 mb-4">
              <v-card-title class="headline">Vue는 무엇일까요?</v-card-title>
              <v-card-text class="body-1">
                Vue(view)는 사용자 인터페이스를 구축하기 위한 프로그레시브 프레임워크입니다. 선언적 렌더링과 컴포넌트 시스템 등...
                <!-- More content -->
              </v-card-text>
            </v-card>
            <!-- Add additional content sections here -->
          </v-col>

          <!-- Main content right column: On this page and Attachments -->
          <v-col cols="12" md="3">
            <v-card flat class="mb-4">
              <v-card-title>이 페이지에서</v-card-title>
              <v-list dense>
                <v-list-item link to="#introduction">소개</v-list-item>
                <v-list-item link to="#whatisvue">Vue는 무엇일까요?</v-list-item>
                <!-- Add other links here -->
              </v-list>
            </v-card>
            <v-card flat>
              <v-card-title>첨부파일</v-card-title>
              <v-list dense>
                <v-list-item href="path/to/first-file.pdf" target="_blank">
                  <v-list-item-action>
                    <v-icon color="blue">mdi-file-pdf-box</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>첫 번째 파일</v-list-item-content>
                </v-list-item>
                <!-- Add other attachment items here -->
              </v-list>
            </v-card>
          </v-col>

        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
export default {
  name: 'MainPage',
  data() {
    return {
      // Component data
    };
  },
  methods: {
    // Component methods
  },
  // You can include component lifecycle hooks if necessary
};
</script>

<style scoped>
.deep-blue {
  background-color: #1E2D3D;
}

/* Add additional styling as needed here */
.v-card--outlined {
  box-shadow: none; /* Only show the card outline, remove shadow */
}

/* Add padding to display card content wider */
.v-card--outlined .v-card__text {
  padding: 16px; /* Increase inner padding */
}
</style>