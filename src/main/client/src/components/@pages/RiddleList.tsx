import React from 'react'
import { Box, Flex, Heading, VStack, Text, useColorModeValue } from '@chakra-ui/react'
import { Page } from 'components/@layout/Page'
import { Link } from 'react-router-dom'

type RiddleListProps = {}
export const mockData = {
  riddles: [
    {
      riddle: {
        id: 1,
        title: 'Találd ki mi van a képen!',
        imageUrl: 'https://place-hold.it/400x400',
        hint: 'Gondolkozz erősebben!',
        solution: 'Vasaló',
        solved: false
      }
    },
    {
      riddle: {
        id: 2,
        title: 'Mi lehet a megoldás?',
        type: 'BOTH'
      }
    },
    {
      riddle: {
        id: 3,
        title: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
        type: 'BOTH'
      }
    },
    {
      riddle: {
        id: 4,
        title: 'Mi lehet a megoldás?',
        type: 'BOTH'
      }
    },
    {
      riddle: {
        id: 4,
        title: 'Mi lehet a megoldás?',
        type: 'BOTH'
      }
    },
    {
      riddle: {
        id: 4,
        title: 'Mi lehet a megoldás?',
        type: 'BOTH'
      }
    },
    {
      riddle: {
        id: 4,
        title: 'Mi lehet a megoldás?',
        type: 'BOTH'
      }
    }
  ]
}

export const RiddleList: React.FC<RiddleListProps> = (props) => {
  const bg = useColorModeValue('gray.200', 'gray.600')

  return (
    <Page {...props}>
      <Heading>Riddleök</Heading>
      <VStack spacing={4} align="stretch">
        {mockData.riddles.map((item) => (
          <Box bg={bg} px={4} py={2} borderRadius="md" _hover={{ bgColor: 'brand.500' }}>
            <Link to={`/riddleok/${item.riddle.id}`} style={{ textDecoration: 'none' }}>
              <Flex align="center">
                <Text fontWeight="bold" fontSize="xl">
                  {item.riddle.title}
                </Text>
              </Flex>
            </Link>
          </Box>
        ))}
      </VStack>
    </Page>
  )
}
