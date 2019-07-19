// Import Vue
import Vue from 'vue';
import VueRouter from 'vue-router';

// Import Vue App, routes, store
import App from './App';
import routes from './routes';

Vue.use(VueRouter);

// Import Bootstrap
import BootstrapVue from 'bootstrap-vue'

Vue.use(BootstrapVue);

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import axios from 'axios';

// Configure router
const router = new VueRouter({
    routes,
    linkActiveClass: 'active',
//    mode: 'history'
});

new Vue({
    el: '#app',
    render: h => h(App),
    router
});
