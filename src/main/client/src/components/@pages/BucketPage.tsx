import { Heading } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'

type BucketPageProps = {}

export const BucketPage: React.FC<BucketPageProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Bucketlist item</Heading>
    </Page>
  )
}
