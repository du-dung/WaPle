<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" persistent max-width="290">
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          dark
          v-bind="attrs"
          v-on="on"
          v-if="modifyThemes.length !== 0"
        >
          수정
        </v-btn>
        <v-btn
          dark
          v-bind="attrs"
          v-on="on"
          v-else
        >
          삭제
        </v-btn>
      </template>
      <v-card>
        <v-card-title class="headline"></v-card-title>
        <v-card-text v-if="modifyThemes.length !== 0"> <b>정말로 수정하시겠습니까?</b></v-card-text>
        <v-card-text v-else> <b>정말로 삭제하시겠습니까?</b></v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="cancle">취소</v-btn>
          <v-btn color="green darken-1" text @click="modify">확인</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import api from '@/utils/api';
import eventBus from '@/utils/EventBus';

export default {
  props: {
    modifyThemes: Array,
    menu: Boolean,
    item: Object,
    original: Array,
    index: Number,
  },
  data() {
    return {
      dialog: false,
      additional: {},
    };
  },
  methods: {
    cancle() {
      this.$emit('update:menu', false);
      this.dialog = false;
    },
    modify() {
      this.$emit('update:menu', false);
      if (this.modifyThemes.length === 0) {
        eventBus.$emit('deleteCard', this.index);
        this.original.forEach((element) => {
          this.additional = { groupId: element.groupId, themeId: element.themeId };
          this.delBookmark();
        });
      } else {
        const add = this.modifyThemes.filter((x) => !this.original.includes(x));
        const del = this.original.filter((x) => !this.modifyThemes.includes(x));
        add.forEach((element) => {
          this.additional = { groupId: element.groupId, themeId: element.themeId };
          this.postBookmark();
        });
        del.forEach((element) => {
          this.additional = { groupId: element.groupId, themeId: element.themeId };
          this.delBookmark();
        });
      }
      this.dialog = false;
    },
    postBookmark() {
      api.post('/bookmarks', {
        address: this.item.address,
        groupId: this.additional.groupId,
        lat: this.item.lat,
        lng: this.item.lng,
        name: this.item.name,
        placeId: this.item.placeId,
        themeId: this.additional.themeId,
        url: this.item.url,
        tel: this.item.tel,
        category: this.item.category,
        userId: this.$session.get('uid'),
      }, {
        headers: {
          token: this.$session.get('token'),
        },
      }).then(() => {
        this.$toast.success('북마크 수정 성공');
      }).catch((err) => {
        console.error(err);
        this.$toast.error('북마크 수정 실패, 다시 시도해주세요.');
      });
    },
    delBookmark() {
      api.delete(`/bookmarks/${this.additional.groupId}/${this.item.placeId}/${this.additional.themeId}`, {
        headers: {
          token: this.$session.get('token'),
        },
      }).then(() => {
        this.$toast.success('북마크 삭제 성공');
      }).catch((err) => {
        console.error(err);
        this.$toast.error('북마크 삭제 실패, 다시 시도해주세요.');
      });
    },
  },
};
</script>

<style>

</style>
