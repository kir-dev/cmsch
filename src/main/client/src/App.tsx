import { ChakraProvider } from '@chakra-ui/react'
import * as React from 'react'
import customTheme from './utils/customTheme'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { Home } from './components/@pages/Home'
import { ResortList } from './components/@pages/ResortList'
import { CommunityPage } from './components/@pages/CommunityPage'
import { ResortPage } from './components/@pages/ResortPage'
import { CommunityList } from './components/@pages/CommunityList'
import { ProfilePage } from 'components/@pages/ProfilePage'
import { AchievementCategoryList } from './components/@pages/AchievementCategoryList'
import { AchievementCategoryPage } from './components/@pages/AchievementCategoryPage'
import { AchievementPage } from './components/@pages/AchievementPage'
import { RiddleCategoryList } from 'components/@pages/RiddleCategoryList'
import { RiddlePage } from 'components/@pages/RiddlePage'
import { QRList } from 'components/@pages/QRList'
import { QRScan } from 'components/@pages/QRScan'
import { QRScanResult } from 'components/@pages/QRScanResult'
import { AuthProvider } from './utils/AuthContext'
import { ErrorPage } from './components/@pages/ErrorPage'
import { ServiceProvider } from './utils/ServiceContext'
import { IndexLayout } from 'components/@layout/IndexLayout'
import { ImpressumPage } from 'components/@pages/ImpressumPage'
import './global.css'
import { EventPage } from 'components/@pages/EventPage'

export function App() {
  return (
    <ChakraProvider theme={customTheme}>
      <BrowserRouter>
        <ServiceProvider>
          <AuthProvider>
            <IndexLayout>
              <Routes>
                <Route path="/">
                  {/*Főoldal*/}
                  <Route index element={<Home />} />
                  {/*Profil*/}
                  <Route path="profil">
                    <Route index element={<ProfilePage />} />
                  </Route>
                  {/*Események*/}
                  <Route path="esemenyek">
                    <Route index element={<EventPage />} />
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
                </Route>
              </Routes>
            </IndexLayout>
          </AuthProvider>
        </ServiceProvider>
      </BrowserRouter>
    </ChakraProvider>
  )
}
