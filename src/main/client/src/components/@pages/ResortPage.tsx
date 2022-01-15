import { Page } from '../@layout/Page'
import { Heading } from '@chakra-ui/react'
import React from 'react'

type ResortPageProps = {}

export const ResortPage: React.FC<ResortPageProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Reszort neve</Heading>
    </Page>
  )
}
