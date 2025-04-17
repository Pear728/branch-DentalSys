import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null
  },
  getters: {
    isAuthenticated: state => !!state.user,
    isPatient: state => state.user && state.user.role === 1,
    isDoctor: state => state.user && state.user.role === 2,
    isAdmin: state => state.user && state.user.role === 3,
    currentUser: state => state.user
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    }
  },
  actions: {
    login({ commit }, { user, token }) {
      commit('SET_USER', user)
      commit('SET_TOKEN', token)
    },
    logout({ commit }) {
      commit('SET_USER', null)
      commit('SET_TOKEN', null)
    },
    updateUser({ commit }, user) {
      commit('SET_USER', user)
    }
  }
})
