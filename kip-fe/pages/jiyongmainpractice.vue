<template>
  <v-app>
    <v-container fluid>
      <v-row no-gutters>
        <!-- 왼쪽 사이드바 영역 -->
        <v-col cols="3">
          <v-list dense class="pa-4">
            <!-- '기계 부서' 타이틀 -->
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="font-weight-bold">기계 부서</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-divider></v-divider>

            <!-- 핵심 가이드 섹션 -->
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title>핵심 가이드</v-list-item-title>
              </v-list-item-content>
            </v-list-item>

            <!-- 소개 섹션 -->
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title>소개</v-list-item-title>
              </v-list-item-content>
            </v-list-item>

            <!-- 다른 메뉴 항목들 -->
            <v-list-item v-for="text in texts" :key="text">
              <v-list-item-content>
                <v-list-item-title>{{ text }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-col>

        <!-- 세로선 -->
        <v-col cols="1">
          <v-divider vertical></v-divider>
        </v-col>

        <!-- 가운데 소개 섹션 -->
        <v-col cols="5" class="text-center">
          <v-divider></v-divider>
          <v-card flat class="mt-4">
            <v-card-title class="headline my-2">소개</v-card-title>
            <!-- 소개 내용을 여기에 추가 -->
          </v-card>
          <v-divider></v-divider> <!-- 가로 선 추가 -->
          <v-card flat class="mt-4">
            <v-card-text>
              <!-- 커다란 공간(문서 등이 들어갈 곳) -->
            </v-card-text>
          </v-card>
        </v-col>

        <!-- 가로선 -->
        <v-col cols="1">
          <v-divider></v-divider>
        </v-col>

        <!-- 오른쪽 영역 -->
        <v-col cols="2">
          <!-- 'On This Page' 섹션 -->
          <v-card flat>
            <v-card-title class="headline my-2 text-right">On This Page</v-card-title>
            <v-card-text>
              <v-list dense>
                <v-list-item v-for="item in rightSideItems" :key="item">
                  <v-list-item-content>
                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                    <v-list-item-subtitle v-if="item.subtitle">{{ item.subtitle }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>

          <!-- 첨부 파일 섹션 -->
          <v-card flat>
            <v-card-title class="headline my-2">첨부 파일</v-card-title>
            <v-card-text>
              <v-btn text color="primary">service-task.pdf</v-btn>
              <v-btn text color="primary">work-project.zip</v-btn>
              <!-- 더 많은 파일들... -->
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      texts: [
        '템플릿문법', '반응형기초', '계산된속성', '조건부렌더링', '리스트렌더링',
        '이벤트 핸들링', 'form 입력바인딩', '생명주기 훅', '감 시 자',
        '템플릿 참조', '컴포넌트 기초', '컴포넌트 심화', '등록', 'props',
        '이벤트', '컴포넌트 v-model', '풀스루 속성', '슬롯', 'provide / inject',
        '비동기 컴포넌트'
      ],
      rightSideItems: [
        { title: 'Vue는 무엇일까요?' },
        { title: '프로그레시브 프레임워크' },
        { title: '싱글 파일 컴포넌트' },
        { title: 'API 스타일' },
        { title: '여전히 궁금한 것이 있나요?', subtitle: '니가 직접 알아보세요!' }
      ],
    };
  },
};
</script>

<style scoped>
.font-weight-bold {
  font-weight: bold;
}
.headline {
  font-size: 1.5rem;
  font-weight: bold;
}
</style>
