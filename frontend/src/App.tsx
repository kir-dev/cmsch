import { Suspense } from 'react'
import { Route, Routes } from 'react-router'
import { useConfigContext } from './api/contexts/config/ConfigContext.tsx'
import { CmschLayout } from './common-components/layout/CmschLayout'
import AccessKeyPage from './pages/access-key/accessKey.page.tsx'
import CommunityPage from './pages/communities/community.page.tsx'
import CommunityListPage from './pages/communities/communityList.page.tsx'
import LikedCommunityListPage from './pages/communities/likedCommunityList.page.tsx'
import OrganizationPage from './pages/communities/organization.page.tsx'
import OrganizationListPage from './pages/communities/organizationList.page.tsx'
import TinderPage from './pages/communities/tinder.page.tsx'
import TinderQuestionsPage from './pages/communities/tinderQuestions.page.tsx'
import TinderRouter from './pages/communities/tinderRouter.tsx'
import CountdownPage from './pages/countdown/countdown.page'
import DebtPage from './pages/debt/debt.page.tsx'
import { ErrorPage } from './pages/error/error.page'
import EventPage from './pages/events/event.page'
import EventCalendarPage from './pages/events/eventCalendar.page.tsx'
import EventListPage from './pages/events/eventList.page'
import ExtraPage from './pages/extra/extra.page.tsx'
import FormPage from './pages/form/form.page.tsx'
import HomePage from './pages/home/home.page'
import ImpressumPage from './pages/impressum/impressum.page.tsx'
import IndexPage from './pages/index/index.page'
import LeaderboardPage from './pages/leaderboard/leaderboard.page.tsx'
import LoginPage from './pages/login/login.page'
import MapPage from './pages/map/map.page.tsx'
import NewsPage from './pages/news/news.page.tsx'
import NewsListPage from './pages/news/newsList.page.tsx'
import { AliasChangePage } from './pages/profile/profile.aliasChange.page.tsx'
import { ProfileGroupChangePage } from './pages/profile/profile.groupChange.page.tsx'
import ProfilePage from './pages/profile/profile.page.tsx'
import QrLevelsPage from './pages/qr-fight/qrLevels.page.tsx'
import FreestyleRacePage from './pages/race/freestyleRace.page.tsx'
import RacePage from './pages/race/race.page.tsx'
import RaceByTeamPage from './pages/race/raceByTeam.page.tsx'
import RiddlePage from './pages/riddle/riddle.page.tsx'
import RiddleCategoryPage from './pages/riddle/riddleCategory.page.tsx'
import RiddleHistoryPage from './pages/riddle/riddleHistory.page.tsx'
import RiddleListPage from './pages/riddle/riddleList.page.tsx'
import TaskPage from './pages/task/task.page.tsx'
import TaskCategoryPage from './pages/task/taskCategory.page.tsx'
import TaskCategoryList from './pages/task/taskCategoryList.page.tsx'
import CreateTeamPage from './pages/teams/createTeam.page.tsx'
import EditMyTeamPage from './pages/teams/editMyTeam.page.tsx'
import MyTeamPage from './pages/teams/myTeam.page.tsx'
import TeamDetailsPage from './pages/teams/teamDetails.page.tsx'
import TeamListPage from './pages/teams/teamList.page.tsx'
import TokenListPage from './pages/token/tokenList.page.tsx'
import TokenScanPage from './pages/token/tokenScan.page.tsx'
import TokenScanResultPage from './pages/token/tokenScanResult.page.tsx'
import { l } from './util/language'
import { Paths } from './util/paths.ts'
import { TitleProvider } from './util/TitleProvider.tsx'

export function App() {
  const appName = useConfigContext()?.components?.app?.siteName || 'CMSch'
  return (
    <TitleProvider titleTemplate={(title) => (title ? `${appName} | ${title}` : appName)}>
      <CountdownPage>
        <CmschLayout>
          <Suspense>
            <Routes>
              <Route path="/">
                <Route path={Paths.HOME}>
                  <Route index element={<HomePage />} />
                </Route>
                <Route path={Paths.ACCESS_KEY} element={<AccessKeyPage />} />
                <Route path={Paths.COMMUNITY}>
                  <Route path=":id" element={<CommunityPage />} />
                  <Route index element={<CommunityListPage />} />
                </Route>
                <Route path={Paths.DEBT}>
                  <Route index element={<DebtPage />} />
                </Route>
                <Route path={Paths.ORGANIZATION}>
                  <Route path=":id" element={<OrganizationPage />} />
                  <Route index element={<OrganizationListPage />} />
                </Route>
                <Route path={Paths.TINDER}>
                  <Route path="question" element={<TinderQuestionsPage />} />
                  <Route path="community" element={<TinderPage />} />
                  <Route path="liked" element={<LikedCommunityListPage />} />
                  <Route index element={<TinderRouter />} />
                </Route>
                <Route path={Paths.EVENTS}>
                  <Route path=":path" element={<EventPage />} />
                  <Route path={Paths.CALENDAR} element={<EventCalendarPage />} />
                  <Route index element={<EventListPage />} />
                </Route>
                <Route path={Paths.EXTRA_PAGE + '/:slug'}>
                  <Route index element={<ExtraPage />} />
                </Route>
                <Route path={Paths.FORM + '/:slug'}>
                  <Route index element={<FormPage />} />
                </Route>
                <Route path={Paths.IMPRESSUM}>
                  <Route index element={<ImpressumPage />} />
                </Route>
                <Route path={Paths.LEADER_BOARD + '/*'} element={<LeaderboardPage />} />
                <Route path={Paths.MAP}>
                  <Route index element={<MapPage />} />
                </Route>
                <Route path={Paths.NEWS}>
                  <Route path=":id" element={<NewsPage />} />
                  <Route index element={<NewsListPage />} />
                </Route>
                <Route path={Paths.PROFILE}>
                  <Route path="change-group" element={<ProfileGroupChangePage />} />
                  <Route path="change-alias" element={<AliasChangePage />} />
                  <Route index element={<ProfilePage />} />
                </Route>
                <Route path={Paths.QR_FIGHT} element={<QrLevelsPage />} />
                <Route path={Paths.RACE}>
                  <Route path="freestyle" element={<FreestyleRacePage />} />
                  <Route path=":category" element={<RacePage />} />
                  <Route index element={<RacePage />} />
                </Route>
                <Route path={Paths.RIDDLE}>
                  <Route path="solve/:id" element={<RiddlePage />} />
                  <Route path="category/:id" element={<RiddleCategoryPage />} />
                  <Route path="history" element={<RiddleHistoryPage />} />
                  <Route index element={<RiddleListPage />} />
                </Route>
                <Route path={Paths.TASKS}>
                  <Route path="category/:id" element={<TaskCategoryPage />} />
                  <Route path=":id" element={<TaskPage />} />
                  <Route index element={<TaskCategoryList />} />
                </Route>
                <Route path={Paths.CREATE_TEAM} element={<CreateTeamPage />} />
                <Route path={Paths.EDIT_TEAM} element={<EditMyTeamPage />} />
                <Route path={Paths.MY_TEAM}>
                  <Route index element={<MyTeamPage />} />
                  <Route path={Paths.RACE} element={<RaceByTeamPage />} />
                </Route>
                <Route path={Paths.TEAMS}>
                  <Route index element={<TeamListPage />} />
                  <Route path="details/:id">
                    <Route index element={<TeamDetailsPage />} />
                    <Route path={Paths.RACE} element={<RaceByTeamPage />} />
                  </Route>
                </Route>
                <Route path={Paths.TOKEN_SCANNED} element={<TokenScanResultPage />} />
                <Route path={Paths.TOKEN}>
                  <Route index element={<TokenListPage />} />
                  <Route path="scan" element={<TokenScanPage />} />
                </Route>
                <Route index element={<IndexPage />} />
                <Route path="login" element={<LoginPage />} />
                <Route path="logout" element={<HomePage />} />
                {/** Error handling pages */}
                <Route path="error" element={<ErrorPage />} />
                <Route path="*" element={<ErrorPage message={l('not-found-message')} />} />
              </Route>
            </Routes>
          </Suspense>
        </CmschLayout>
      </CountdownPage>
    </TitleProvider>
  )
}
