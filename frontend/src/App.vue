<template>
  <v-app>
    <v-overlay
      opacity = 0.46
      z-index = 100
      :value="overlay"
    >
      <div class="loading">
        <i class="fa fa-spinner fa-spin"></i>Loading
      </div>
    </v-overlay>
    <router-view></router-view>
  </v-app>
</template>

<script>
import EventBus from '@/utils/EventBus';

export default {
  props: {
    source: String,
  },
  data() {
    return {
      overlay: false,
    };
  },
  created() {
    // 사파리일경우 하단에 잘리는 현상으로 인해 동적 클래스 바인딩해줄 필요가 있음
    const agent = navigator.userAgent.toLowerCase();
    if (agent.indexOf('safari') !== -1) {
      this.$store.dispatch('updateSafari', true);
    }
    EventBus.$on('showOverlay', () => {
      this.overlay = true;
    });
    EventBus.$on('closeOverlay', () => {
      this.overlay = false;
    });
  },
};
</script>

<style lang="scss">
@font-face { font-family: 'IBMPlexSansKR-Regular'; src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_20-07@1.0/IBMPlexSansKR-Regular.woff') format('woff'); font-weight: normal; font-style: normal; }
@import url(//fonts.googleapis.com/earlyaccess/hanna.css);
@font-face {
  font-family: 'GmarketSansBold';
  src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2001@1.1/GmarketSansBold.woff') format('woff');
  font-weight: normal;
  font-style: normal;
}
@font-face {
  font-family: 'GmarketSansMedium';
  src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2001@1.1/GmarketSansMedium.woff') format('woff');
  font-weight: normal;
  font-style: normal;
}

* {
  font-family: 'IBMPlexSansKR-Regular', sans-serif !important;
  .headline {
    font-family: 'Hanna', sans-serif !important;
  }
}

.loading {
  background-color: #d2d2d4;
  opacity: 0.8;
  text-align: center;
  position: absolute;
  color: #fff;
  z-index: 9;
  background: grey;
  padding: 8px 18px;
  border-radius: 5px;
  left: calc(50% - 45px);
  top: calc(50% - 18px);
}
</style>
