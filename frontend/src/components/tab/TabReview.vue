<template>
<v-main>
  <v-container
    align='center'
    justify='center'
    id="scroll-target"
    style="height: calc(90vh - 4rem)"
    class="overflow-y-auto"
    :class="{ safari: isSafari && $vuetify.breakpoint.mdAndDown}"
  >
    <transition name="fade">
      <div class="loading" v-show="loading">
        <span class="fa fa-spinner fa-spin"></span> Loading
      </div>
    </transition>
    <v-divider style="position: relative;top: 0rem; margin: 0;"></v-divider>
    <div v-if="items.length == 0"
      class="justify-space-between v-card__text"
      style="color: gray">
      아직 작성된 리뷰가 없는 것 같아요! <br>
      <br>
      북마크 탭에서 <v-icon style="font-size: 1rem;">mdi-pencil-plus-outline</v-icon> 버튼으로 <br>
      새로운 리뷰를 쓸 수 있어요 :)
    </div>
    <v-row
      align='center'
      justify='center'
      v-scroll:#scroll-target="onScroll"
    >
      <v-col
        v-for="(item, i) in items"
        :key="i"
        cols="12"
        style="padding: 3px; height: 5.1rem;"
      >
        <v-card
          @click="infowindow(i)"
          style="height: 5rem; box-shadow: none !important;"
          tile
        >
          <div class="d-flex flex-no-wrap justify-space-between">
            <div>
              <v-card-title
                class="headline"
                v-text="item.name"
                style="font-size: 1rem !important; padding-top: 0.5rem; padding-bottom: 0;"
              >
                </v-card-title>
                <v-card-actions style="padding-bottom: 0px;">
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        icon
                        v-bind="attrs"
                        v-on="on"
                        @click.stop="showDialog(item)"
                      >
                        <v-icon style="font-size: 1rem;">mdi-calendar-plus</v-icon>
                      </v-btn>
                    </template>
                    <span>약속 추가</span>
                  </v-tooltip>
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        icon
                        v-bind="attrs"
                        v-on="on"
                        @click.stop="readReview(item)"
                      >
                        <v-badge
                          color="blue"
                          :content="item.count"
                        >
                          <v-icon style="font-size: 1rem;">mdi-text-box-multiple-outline</v-icon>
                        </v-badge>
                      </v-btn>
                    </template>
                    <span>리뷰 읽기</span>
                  </v-tooltip>
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        icon
                        v-bind="attrs"
                        v-on="on"
                        @click.stop="writeReview(item)"
                      >
                        <v-icon style="font-size: 1rem;">mdi-pencil-plus-outline</v-icon>
                      </v-btn>
                    </template>
                    <span>리뷰 쓰기</span>
                  </v-tooltip>
                </v-card-actions>
              </div>
          </div>
          <v-divider style="position: relative; top: -1.2rem;"></v-divider>
        </v-card>
      </v-col>
    </v-row>
    <appointment-modal
      :appointmentDialog="appointmentDialog"
      @closeAppointmentModal="appointmentDialog = !appointmentDialog"
    />
  </v-container>
</v-main>
</template>

<script>
import store from '@/store/index';
import api from '@/utils/api';
import EventBus from '@/utils/EventBus';

export default {
  data() {
    return {
      items: [],
      bottom: false,
      loading: false,
      noData: false,
      offset: 1,
      limit: 10,
      appointmentDialog: false,
    };
  },
  components: {
    AppointmentModal: () => import('@/components/items/AppointmentModal.vue'),
  },
  created() {
    store.dispatch('invisibleBookmark');
    EventBus.$emit('toggle-drawer-1');
  },
  mounted() {
    this.readAllReview();
  },
  computed: {
    isSafari: () => store.getters.isSafari,
  },
  watch: {
    bottom() {
      if (this.bottom && !this.noData) {
        this.noData = true;
        this.offset += 1;
        this.bottom = false;
        this.readAllReview();
      }
    },
  },
  methods: {
    infowindow(index) {
      EventBus.$emit('moveMap', { lat: this.items[index].lat, lng: this.items[index].lng, index });
    },
    showDialog(item) {
      store.dispatch('selectPlace', item);
      store.dispatch('getGroups');
      store.dispatch('getAppointments');
      this.appointmentDialog = !this.appointmentDialog;
    },
    readReview(item) {
      this.$store.dispatch('updateItem', item);
      this.$router.push('./reviewlist');
    },
    writeReview(item) {
      store.dispatch('updateItem', item);
      store.dispatch('changeWriteDialog');
      store.dispatch('getGroups');
    },
    onScroll(e) {
      const { scrollTop, clientHeight, scrollHeight } = e.target;
      if (scrollTop + clientHeight >= scrollHeight) {
        this.bottom = true;
      }
    },
    readAllReview() {
      api.get(`/reviews/all/${this.$session.get('uid')}/${this.limit}/${this.offset}`, {
        headers: {
          token: this.$session.get('token'),
        },
      }).then(({ data }) => {
        this.loading = true;
        setTimeout(() => {
          if (data.length === 0) {
            this.noData = true;
          } else {
            this.noData = false;
            this.items = this.items.concat(data);
            this.$store.dispatch('doUpdate', this.items);
          }
          this.loading = false;
        }, 500);
      }).catch((err) => {
        console.error(err);
        this.$toast.error('리뷰 목록 조회 실패, 다시 시도해주세요.');
      });
    },
  },
};
</script>

<style scoped>
.v-main {
  padding-top: 0px !important;
}
.d-flex.flex-no-wrap.justify-space-between:hover{
  background-color: #d2d2d4;
  opacity: 0.8;
}
.loading {
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
.fade-enter-active, .fade-leave-active {
  transition: opacity .5s
}
.fade-enter, .fade-leave-to {
  opacity: 0
}
.safari {
  height: calc(75vh - 50px) !important;
}
</style>
