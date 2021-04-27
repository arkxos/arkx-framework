<template>
  <img v-if="isExternal" class="img-icon" :src="icon" />
  <svg v-else-if="isCustomSvg" :class="svgClass" aria-hidden="true" v-on="$listeners">
    <use :xlink:href="'#vab-icon-' + icon" />
  </svg>
  <svg v-else-if="isDefaultSvg" class="vab-icon" aria-hidden="true" v-on="$listeners">
    <use :xlink:href="remixIconPath + '#ri-' + icon" />
  </svg>
  <i v-else :class="'ri-' + icon"></i>
</template>

<script>
  import { isExternal } from '@/utils/validate'

  export default {
    name: 'ArkIcon',
    props: {
      icon: {
        type: String,
        required: true
      },
      isCustomSvg: {
        type: Boolean,
        default: false
      },
      isDefaultSvg: {
        type: Boolean,
        default: false
      },
      className: {
        type: String,
        default: ''
      }
    },
    data: function () {
      return {
        remixIconPath: '@/components/ArkIcon/remixicon.symbol.f09b1c74.svg' // n("adf1")
      }
    },
    computed: {
      isExternal: function () {
        return isExternal(this.icon)
      },
      svgClass: function () {
        return this.className ? 'vab-icon '.concat(this.className) : 'vab-icon'
      }
    }
  }
</script>

<style scoped></style>
