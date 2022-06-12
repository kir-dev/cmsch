import { Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'

export const ProfilePage = () => (
  <CmschPage>
    <Helmet />
    <Heading>Profile</Heading>
  </CmschPage>
)
