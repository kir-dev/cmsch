import { ChakraProvider } from '@chakra-ui/react'
import * as React from 'react'

import customTheme from './utils/customTheme'
import { IndexLayout } from './components/@layout/IndexLayout'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { Home } from './components/@pages/Home'
import { ResortList } from './components/@pages/ResortList'
import { CommunityPage } from './components/@pages/CommunityPage'
import { ResortPage } from './components/@pages/ResortPage'
import { CommunityList } from './components/@pages/CommunityList'
import { AchievementList } from './components/@pages/AchievementList'
import { AchievementPage } from './components/@pages/AchievementPage'

export function App() {
  return (
    <ChakraProvider theme={customTheme}>
      <BrowserRouter>
        <IndexLayout>
          <Routes>
            <Route path="/">
              {/*Főoldal*/}
              <Route index element={<Home />} />
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
              {/*BucketList*/}
              <Route path="bucketlist">
                <Route path=":id" element={<AchievementPage />} />
                <Route index element={<AchievementList />} />
              </Route>
              {/*QR*/}
            </Route>
          </Routes>
        </IndexLayout>
      </BrowserRouter>
    </ChakraProvider>
  )
}
