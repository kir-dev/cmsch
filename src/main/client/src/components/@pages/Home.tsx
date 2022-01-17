import { Alert, AlertIcon, Button, ButtonGroup, Heading } from '@chakra-ui/react'
import { PlusSquareIcon } from '@chakra-ui/icons'
import { Paragraph } from '../@commons/Basics'
import { Page } from '../@layout/Page'
import React from 'react'

export const Home: React.FC = () => {
  return (
    <Page>
      <Heading>Üdvözlünk a GólyaKörTe portálon</Heading>
      <Alert status="warning" variant="left-accent">
        <AlertIcon />
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam velit nisi, facilisis id iaculis eu, pretium sed diam. Sed sit amet
        nibh non felis venenatis gravida. Aenean semper accumsan ante varius gravida.
      </Alert>
      <Paragraph>
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam velit nisi, facilisis id iaculis eu, pretium sed diam. Sed sit amet
        nibh non felis venenatis gravida. Aenean semper accumsan ante varius gravida.
      </Paragraph>
      <ButtonGroup>
        <Button colorScheme="brand" leftIcon={<PlusSquareIcon />}>
          Click me
        </Button>
      </ButtonGroup>
    </Page>
  )
}
