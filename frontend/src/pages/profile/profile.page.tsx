import { Heading, Text } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'

const ProfilePage = () => {
  const { profile } = useAuthContext()

  return (
    <CmschPage>
      <Helmet />
      <Heading>Profile</Heading>
      <Text>{profile?.fullName}</Text>
    </CmschPage>
  )
}

export default ProfilePage
