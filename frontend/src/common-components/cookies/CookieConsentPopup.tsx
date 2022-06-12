import { Alert, AlertDescription, AlertIcon, AlertTitle, Box, Button, Flex } from '@chakra-ui/react'

type Props = {
  onClick: () => void
}

export const CookieConsentPopup = ({ onClick }: Props) => (
  <Box maxWidth="80rem" mx="auto" p={2}>
    <Alert display={{ base: 'block', md: 'flex' }} colorScheme="brand" variant="solid" borderRadius={6} width="full">
      <Flex flex={1}>
        <AlertIcon alignSelf="flex-start" />
        <Box>
          <AlertTitle>Fogadd el cookie-jainkat!</AlertTitle>
          <AlertDescription display="block">
            Ezen az oldalon cookie-kat használunk a megfelelő működés érdekében. A weboldal használatával ebbe beleegyezel.
          </AlertDescription>
        </Box>
      </Flex>
      <Flex justifyContent="flex-end">
        <Button colorScheme="blackAlpha" onClick={onClick} ml={2} mt={{ base: 2, md: 0 }}>
          Megértettem.
        </Button>
      </Flex>
    </Alert>
  </Box>
)
