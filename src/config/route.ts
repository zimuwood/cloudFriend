import Index from '../pages/Index.vue'
import Team from '../pages/TeamPage.vue'
import User from '../pages/UserPage.vue'
import Search from '../pages/SearchPage.vue'
import UserEditPage from "../pages/UserEdit/UserEditPage.vue";
import UserAvatar from "../pages/UserEdit/UserAvatar.vue";
import UserGender from "../pages/UserEdit/UserGender.vue";
import UserTags from "../pages/UserEdit/UserTags.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import LoginPage from "../pages/LoginPage.vue";
import BasicLayout from "../layouts/BasicLayout.vue";
import EditLayout from "../layouts/EditLayout.vue";
import RegisterPage from "../pages/RegisterPage.vue";
import DetailLayout from "../layouts/DetailLayout.vue";
import TeamInfo from "../pages/TeamInfo.vue";
import UserInfo from "../pages/UserInfo.vue";
import TeamEditPage from "../pages/TeamEditPage.vue";
import BlankLayout from "../layouts/BlankLayout.vue";
import TagEditPage from "../pages/TagEditPage.vue";

const routes = [
    { path: '/login', component: LoginPage},
    { path: '/register', component: RegisterPage},
    {
        path: '/base',
        component: BasicLayout,
        children: [
            { path: '/', component: Index },
            { path: '/team', component: Team },
            { path: '/user', component: User},
            { path: '/search', component: Search},
            { path: '/user/list/:id', component: SearchResultPage},
        ]
    },
    {
        path: '/edit',
        component: EditLayout,
        children: [
            { path: '/user/edit', component: UserEditPage },
            { path: '/user/edit/avatar', component: UserAvatar},
            { path: '/user/edit/gender', component: UserGender},
            { path: '/user/edit/tags', component: UserTags},
            { path: '/team/edit', component: TeamEditPage},
        ]
    },
    {
        path: '/detail',
        component: DetailLayout,
        children: [
            { path: '/userInfo', component: UserInfo},
            { path: '/teamInfo', component: TeamInfo},
        ]
    },{
        path: '/blank',
        component: BlankLayout,
        children: [
            { path: '/createTag', component: TagEditPage },
        ]
    }
]

export default routes;