import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const OrganizationPage = lazy(() => import('../pages/communities/organization.page'))
const OrganizationListPage = lazy(() => import('../pages/communities/organizationList.page'))
const CommunityPage = lazy(() => import('../pages/communities/community.page'))
const CommunityListPage = lazy(() => import('../pages/communities/communityList.page'))

export function CommunitiesModule() {
  return (
    <>
      <Route path={Paths.COMMUNITY}>
        <Route path=":id" element={<CommunityPage />} />
        <Route index element={<CommunityListPage />} />
      </Route>
      <Route path={Paths.ORGANIZATION}>
        <Route path=":id" element={<OrganizationPage />} />
        <Route index element={<OrganizationListPage />} />
      </Route>
    </>
  )
}
