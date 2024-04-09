<template>
  <v-app>
    <!-- 사이드바 -->
    <v-navigation-drawer app permanent>
      <v-list>
        <v-list-item v-for="item in menuItems" :key="item.text" link>
          <v-list-item-icon>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ item.text }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- 앱 바 -->
    <v-app-bar app>
      <!-- 앱 바 내용, 예를 들어 로고나 타이틀, 메뉴 버튼 등이 위치할 수 있습니다. -->
    </v-app-bar>

    <!-- 메인 컨텐츠 영역 -->
    <v-main>
      <v-container>
        <v-row>
          <v-col cols="12" md="8">
            <!-- 문서 내용 -->
            <v-card outlined>
              <v-card-title class="text-h5 py-4">Vue는 무엇일까요?</v-card-title>
              <v-card-text>
                <div>Vue는 프로그레시브 자바스크립트 프레임워크입니다...</div>
                <!-- 실제 콘텐츠가 여기에 들어갑니다. -->
              </v-card-text>
            </v-card>
          </v-col>

          <v-col cols="12" md="4">
            <!-- 오른쪽 사이드바, 페이지 내 목차나 관련 문서 링크 -->
            <v-card outlined class="mb-4">
              <v-card-title>목차</v-card-title>
              <v-card-text>
                <v-list dense>
                  <v-list-item v-for="section in sections" :key="section.title" link>
                    <v-list-item-content>
                      <v-list-item-title>{{ section.title }}</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
            <v-card outlined>
              <v-card-title class="px-4 pt-4">첨부 파일</v-card-title>
              <v-list dense>
                <v-list-item v-for="download in downloads" :key="download.name" link>
                  <v-list-item-content>
                    <v-list-item-title>{{ download.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ download.date }}</v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-action>
                    <v-icon color="primary">mdi-download</v-icon>
                  </v-list-item-action>
                </v-list-item>
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
  name: 'App',
  data() {
    return {
      menuItems: [
        // 메뉴 아이템들의 데이터
      ],
      sections: [
        // 문서 섹션들의 데이터
      ],
      downloads: [
        // 다운로드 파일들의 데이터
      ],
    };
  },
  methods: {
    // 이곳에 필요한 메소드를 추가하세요.
  },
};
</script>

<style scoped>
/* 여기에 필요한 스타일을 추가하세요. */
</style>