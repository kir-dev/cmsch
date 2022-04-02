import { ImpressumPage } from 'components/@pages/ImpressumPage'
import { ProfilePage } from 'components/@pages/ProfilePage'
import { QRList } from 'components/@pages/QRList'
import { QRScan } from 'components/@pages/QRScan'
import { QRScanResult } from 'components/@pages/QRScanResult'
import { RiddleCategoryList } from 'components/@pages/RiddleCategoryList'
import { RiddlePage } from 'components/@pages/RiddlePage'
import { Route, Routes } from 'react-router-dom'
import { AUTHSCH_REDIRECT_PATH } from 'utils/configurations'
import { AchievementCategoryList } from './components/@pages/AchievementCategoryList'
import { AchievementCategoryPage } from './components/@pages/AchievementCategoryPage'
import { AchievementPage } from './components/@pages/AchievementPage'
import { CommunityList } from './components/@pages/CommunityList'
import { CommunityPage } from './components/@pages/CommunityPage'
import { ErrorPage } from './components/@pages/ErrorPage'
import { GroupSelectionPage } from './components/@pages/GroupSelectionPage'
import { Home } from './components/@pages/Home'
import { ResortList } from './components/@pages/ResortList'
import { ResortPage } from './components/@pages/ResortPage'
import './global.css'

export const App = () => (
  <Routes>
    <Route path="/">
      {/*Főoldal*/}
      <Route index element={<Home />} />
      {/*Profil*/}
      <Route path="profil">
        <Route path="tankor-modositas" element={<GroupSelectionPage />} />
        <Route index element={<ProfilePage />} />
      </Route>
      {/*Reszortok*/}
      <Route path="reszortok">
        <Route path=":name">
          <Route index element={<ResortPage />} />
        </Route>
        <Route index element={<ResortList />} />
      </Route>
      {/*Körök*/}
      <Route path="korok">
        <Route path=":name" element={<CommunityPage />} />
        <Route index element={<CommunityList />} />
      </Route>
      {/*Riddle*/}
      <Route path="riddleok">
        <Route path=":id" element={<RiddlePage />} />
        <Route index element={<RiddleCategoryList />} />
      </Route>
      {/*BucketList*/}
      <Route path="bucketlist">
        <Route path="kategoria/:id" element={<AchievementCategoryPage />} />
        <Route path=":id" element={<AchievementPage />} />
        <Route index element={<AchievementCategoryList />} />
      </Route>
      {/*QR*/}
      <Route path="qr-scanned" element={<QRScanResult />} />
      <Route path="qr">
        <Route index element={<QRList />} />
        <Route path="scan" element={<QRScan />} />
      </Route>
      {/*Impressum*/}
      <Route path="impresszum">
        <Route index element={<ImpressumPage />} />
      </Route>
      {/*Error*/}
      <Route path="error">
        <Route index element={<ErrorPage />} />
      </Route>
      {/*Authsch visszateres*/}
      <Route path={AUTHSCH_REDIRECT_PATH}>
        <Route index element={<ErrorPage />} />
      </Route>
    </Route>
  </Routes>
)
