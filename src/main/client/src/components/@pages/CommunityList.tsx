import { Heading } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'

type CommunityListProps = {}

export const CommunityList: React.FC<CommunityListProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Körök</Heading>
    </Page>
  )
}
