<template>
  <v-app>
    <!-- 사이드바 -->
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
        <!-- 기타 사이드바 아이템들 -->
      </v-list>
    </v-navigation-drawer>

    <!-- 메인 콘텐츠 -->
    <v-main>
      <v-container fluid>
        <v-row>
          <!-- 메인 콘텐츠 왼쪽 칼럼: 소개 및 Vue는 무엇일까요? -->
          <v-col cols="12" md="9">
            <v-card flat class="mb-4">
              <v-card-title class="headline">소개</v-card-title>
              <!-- 소개 섹션 내용 -->
            </v-card>
            <v-card outlined class="px-5 py-3">
              <v-card-title class="headline">Vue는 무엇일까요?</v-card-title>
              <v-card-text class="body-1">
                Vue(view)는 사용자 인터페이스를 구축하기 위한 프로그레시브 프레임워크입니다. 선언적 렌더링과 컴포넌트 시스템 등...
                <!-- 더 많은 내용 -->
              </v-card-text>
            </v-card>
            <!-- 추가 콘텐츠 섹션 -->
          </v-col>

          <!-- 메인 콘텐츠 오른쪽 칼럼: 이 페이지에서 및 첨부파일 -->
          <v-col cols="12" md="3">
            <v-card flat class="mb-4">
              <v-card-title>이 페이지에서</v-card-title>
              <v-list dense>
                <v-list-item link to="#introduction">소개</v-list-item>
                <v-list-item link to="#whatisvue">Vue는 무엇일까요?</v-list-item>
                <!-- 기타 링크들 -->
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
                <!-- 기타 첨부파일 아이템들 -->
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
      // 컴포넌트 데이터
    };
  },
  methods: {
    // 컴포넌트 메소드
  },
  // 필요한 경우 컴포넌트 라이프사이클 훅 등을 포함할 수 있습니다.
};
</script>

<style scoped>
.deep-blue {
  background-color: #1E2D3D;
}

/* 추가 스타일링이 필요하면 여기에 정의하세요 */
.v-card--outlined {
  box-shadow: none; /* 카드 경계선만 표시하고 그림자는 제거 */
}

/* 패딩 추가로 카드 내용을 좀 더 넓게 표시 */
.v-card--outlined .v-card__text {
  padding: 16px; /* 내부 패딩을 늘립니다 */
}
</style>