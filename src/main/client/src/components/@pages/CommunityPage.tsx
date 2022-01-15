import { Page } from '../@layout/Page'
import { Heading } from '@chakra-ui/react'
import React from 'react'

type CommunityPageProps = {}

export const CommunityPage: React.FC<CommunityPageProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Kör neve</Heading>
    </Page>
  )
}
