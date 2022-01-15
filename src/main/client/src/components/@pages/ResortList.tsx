import { Page } from '../@layout/Page'
import { Heading } from '@chakra-ui/react'
import React from 'react'

type ResortListProps = {}

export const ResortList: React.FC<ResortListProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Reszortok</Heading>
    </Page>
  )
}
