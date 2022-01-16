import { Box, Heading, StackDivider, VStack, Text } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'

type BucketListProps = {}

const mockData = {
  bucketlist: [
    {
      id: 1,
      name: 'Lorem ipsum',
      status: 'Not submitted',
      description: 'blalblal'
    },
    {
      id: 2,
      name: 'Lorem ipsum2',
      status: 'Accepted',
      description: 'fdsfdsgsd'
    }
  ]
}

export const BucketList: React.FC<BucketListProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Bucketlist</Heading>
      <VStack divider={<StackDivider borderColor="gray.200" />} spacing={4} align="stretch">
        {mockData.bucketlist.map((item) => (
          <Box>
            <Text fontSize="lg">{item.name}</Text>
          </Box>
        ))}
      </VStack>
    </Page>
  )
}
