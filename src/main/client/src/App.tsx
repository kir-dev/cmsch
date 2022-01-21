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
import { AchievementList } from './components/@pages/AchievementList'
import { AchievementPage } from './components/@pages/AchievementPage'
import { RiddleCategoryList } from 'components/@pages/RiddleCategoryList'
import { RiddlePage } from 'components/@pages/RiddlePage'
import { QRList } from 'components/@pages/QRList'
import { QRScan } from 'components/@pages/QRScan'
import { QRScanResult } from 'components/@pages/QRScanResult'
import { AuthProvider } from './utils/AuthContext'

export function App() {
  return (
    <ChakraProvider theme={customTheme}>
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/">
              {/*Főoldal*/}
              <Route index element={<Home />} />
              {/*Profil*/}
              <Route path="profil">
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
              <Route path="riddles">
                <Route path=":id" element={<RiddlePage />} />
                <Route index element={<RiddleCategoryList />} />
              </Route>
              {/*BucketList*/}
              <Route path="bucketlist">
                <Route path=":id" element={<AchievementPage />} />
                <Route index element={<AchievementList />} />
              </Route>
              {/*QR*/}
              <Route path="qr-scanned" element={<QRScanResult />}></Route>
              <Route path="qr">
                <Route index element={<QRList />} />
                <Route path="scan" element={<QRScan />}></Route>
              </Route>
            </Route>
          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </ChakraProvider>
  )
}
