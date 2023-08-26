import { Box, Heading, HStack, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { FaFacebook, FaPhone } from 'react-icons/fa'
import { LinkButton } from '../../../common-components/LinkButton'
import { ProfileView } from '../../../util/views/profile.view'

export const GroupLeaderContactList = ({ profile }: { profile: ProfileView }) => {
  const bg = useColorModeValue('#00000005', '#FFFFFF05')
  if (!profile?.groupLeaders?.length || profile.groupLeaders.length === 0) return null
  return (
    <Box mt={5}>
      <VStack gap={5} align="flex-start">
        <Heading size="md">Csoporthoz tartozó elérhetőségek</Heading>
        {profile.groupLeaders.map((gl, index) => (
          <HStack
            borderRadius="md"
            bg={bg}
            p={3}
            gap={3}
            key={index}
            w="full"
            justify="space-between"
            flexDirection={['column', null, 'row']}
          >
            <Text>{gl.name}</Text>
            <HStack gap={3} flexDirection={['column', 'row']}>
              {gl.facebookUrl && (
                <LinkButton leftIcon={<FaFacebook />} href={gl.facebookUrl} external newTab>
                  Facebook
                </LinkButton>
              )}
              {gl.mobilePhone && (
                <LinkButton leftIcon={<FaPhone />} href={'tel:' + gl.mobilePhone} external>
                  {gl.mobilePhone}
                </LinkButton>
              )}
            </HStack>
          </HStack>
        ))}
      </VStack>
    </Box>
  )
}
