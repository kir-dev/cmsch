import { ProfileView } from '../../../util/views/profile.view'
import { Box, Flex, Text } from '@chakra-ui/react'
import QRCode from 'react-qr-code'

export const ProfileQR = ({ profile }: { profile: ProfileView }) => {
  return (
    <Box mt={5}>
      <Text fontSize="xl">QR kódom</Text>
      <Flex justifyContent={{ base: 'center', md: 'flex-start' }}>
        <Box w="fit-content" maxW="100%" p={2} mt={2} borderRadius={3} backgroundColor="white">
          <QRCode value={profile.cmschId} />
        </Box>
      </Flex>
    </Box>
  )
}
