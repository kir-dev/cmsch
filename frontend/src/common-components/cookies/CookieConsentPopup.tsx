import { Alert, AlertDescription, AlertIcon, AlertTitle, Box, Button, Flex } from '@chakra-ui/react'
import { l } from '../../util/language'

type Props = {
  onClick: () => void
}

export const CookieConsentPopup = ({ onClick }: Props) => (
  <Box maxWidth="80rem" mx="auto" p={2}>
    <Alert display={{ base: 'block', md: 'flex' }} status="info" variant="solid" borderRadius={6} width="full">
      <Flex flex={1}>
        <AlertIcon alignSelf="flex-start" />
        <Box>
          <AlertTitle>{l('cookie-consent-title')}</AlertTitle>
          <AlertDescription display="block">{l('cookie-consent-description')}</AlertDescription>
        </Box>
      </Flex>
      <Flex justifyContent="flex-end">
        <Button variant="outline" _hover={{ bgColor: 'blue.600' }} onClick={onClick} ml={2} mt={{ base: 2, md: 0 }}>
          Elfogadom
        </Button>
      </Flex>
    </Alert>
  </Box>
)
