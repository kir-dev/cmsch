import React from 'react'
import { Box, Flex, Heading, VStack, Text, useColorModeValue } from '@chakra-ui/react'
import { Page } from 'components/@layout/Page'
import { Link } from 'react-router-dom'
import { RiddleCategory } from 'types/dto/riddles'
import axios from 'axios'
import { API_BASE_URL } from 'utils/configurations'

type RiddleListProps = {}

function progress(riddleCategory: RiddleCategory) {
  return riddleCategory.completed / riddleCategory.total
}

export const RiddleCategoryList: React.FC<RiddleListProps> = (props) => {
  const bg = useColorModeValue('gray.200', 'gray.600')

  const [riddleCategoryList, setRiddleCategoryList] = React.useState<RiddleCategory[]>([])

  React.useEffect(() => {
    axios.get<RiddleCategory[]>(`${API_BASE_URL}/api/riddle`).then((res) => {
      console.log(res)
      setRiddleCategoryList(res.data)
    })
  }, [setRiddleCategoryList])

  function progressGradient(progress: number, color: string) {
    const endDeg = 360 * progress
    if (progress == 1) {
      return `conic-gradient(${color} 0deg, ${color} 360deg)`
    }
    if (progress == 0) {
      return `conic-gradient(grey 0deg, gray 360deg)`
    }
    return `conic-gradient(grey 0deg,${color} 10deg, ${color} ${endDeg}deg, grey ${endDeg + 10}deg)`
  }

  return (
    <Page {...props}>
      <Heading>Riddleök</Heading>
      <VStack spacing={4} mt={5} align="stretch">
        {riddleCategoryList.map((riddleCategory) => (
          <Box bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: 'brand.500' }}>
            <Link to={`/riddleok/${riddleCategory.nextRiddle}`} style={{ textDecoration: 'none' }}>
              <Flex align="center" justifyContent="space-between">
                <Text fontWeight="bold" fontSize="xl">
                  {riddleCategory.title}
                </Text>
                <Box bgGradient={progressGradient(progress(riddleCategory), 'brand.600')} px={1} py={1} borderRadius="6px">
                  <Text bg={bg} px={4} py={2} borderRadius="6px" fontWeight="bold">
                    {riddleCategory.completed} / {riddleCategory.total}
                  </Text>
                </Box>
              </Flex>
            </Link>
          </Box>
        ))}
      </VStack>
    </Page>
  )
}
