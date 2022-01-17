import { ChakraProvider } from '@chakra-ui/react'
import * as React from 'react'
import '@fontsource/raleway/300.css'
import '@fontsource/raleway/400.css'
import '@fontsource/raleway/700.css'
import '@fontsource/commissioner/300.css'
import '@fontsource/commissioner/400.css'
import '@fontsource/commissioner/700.css'

import customTheme from './utils/customTheme'
import { IndexLayout } from './components/@layout/IndexLayout'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { Home } from './components/@pages/Home'
import { ResortList } from './components/@pages/ResortList'
import { CommunityPage } from './components/@pages/CommunityPage'
import { ResortPage } from './components/@pages/ResortPage'
import { CommunityList } from './components/@pages/CommunityList'
import { ProfilePage } from 'components/@pages/ProfilePage'
import { AchievementList } from './components/@pages/AchievementList'
import { AchievementPage } from './components/@pages/AchievementPage'
import { RiddleList } from 'components/@pages/RiddleList'
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
            <IndexLayout>
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
                    <Route path="riddleok">
                        <Route path=":id" element={<RiddlePage />} />
                        <Route index element={<RiddleList />} />
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
            </IndexLayout>
          </AuthProvider>
      </BrowserRouter>
    </ChakraProvider>
  )
}
