import { Avatar, Box, Button, Heading, HStack, Icon, Stack } from '@chakra-ui/react'
import { FaRegHandPeace, FaSignInAlt } from 'react-icons/fa'
import { useAuthContext } from '../../../../api/contexts/auth/useAuthContext'
import { Menu } from '../../../../api/contexts/config/types'

type Props = {
  onLoginClick: () => void
  loginMenu: Menu
}

export const SidebarFooter = ({ onLoginClick, loginMenu }: Props) => {
  const { isLoggedIn, profile } = useAuthContext()

  if (isLoggedIn)
    return (
      <Stack w="full" p={4} alignItems="stretch">
        <HStack justifyContent="center">
          <Icon as={FaRegHandPeace} w={4} h={4} />
          <Box>Üdv újra itt,</Box>
        </HStack>
        <HStack justifyContent="center">
          <Avatar name={profile?.fullName} size="sm" />
          <Heading as="h3" fontSize="xl">
            {profile?.fullName}!
          </Heading>
        </HStack>
      </Stack>
    )

  return (
    <Stack w="full" p={4} alignItems="stretch">
      <Box textAlign="center">Még nem jelentkezett be!</Box>
      <Button onClick={onLoginClick} variant="outline" size="lg" leftIcon={<FaSignInAlt />}>
        {loginMenu.name}
      </Button>
    </Stack>
  )
}
