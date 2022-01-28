import React from 'react'
import { Box, Flex, Heading, VStack, Text, useColorModeValue, useToast } from '@chakra-ui/react'
import { Page } from 'components/@layout/Page'
import { useNavigate } from 'react-router-dom'
import { RiddleCategory } from 'types/dto/riddles'
import axios from 'axios'
import { API_BASE_URL } from 'utils/configurations'
import { Loading } from '../../utils/Loading'
import { useServiceContext } from '../../utils/useServiceContext'
import { Helmet } from 'react-helmet'

type RiddleListProps = {}

function progress(riddleCategory: RiddleCategory) {
  if (riddleCategory.completed === 0) {
    return 0
  }
  return riddleCategory.completed / riddleCategory.total
}

export const RiddleCategoryList: React.FC<RiddleListProps> = (props) => {
  const bg = useColorModeValue('gray.200', 'gray.600')
  const navigate = useNavigate()
  const toast = useToast()
  const { throwError } = useServiceContext()

  const [riddleCategoryList, setRiddleCategoryList] = React.useState<RiddleCategory[]>([])
  const [loading, setLoading] = React.useState<boolean>(false)

  React.useEffect(() => {
    setLoading(true)
    axios
      .get<RiddleCategory[]>(`${API_BASE_URL}/api/riddle`)
      .then((res) => {
        setRiddleCategoryList(res.data)
        setLoading(false)
      })
      .catch(() => {
        throwError('Nem sikerült lekérni a Riddle-öket.')
      })
  }, [setRiddleCategoryList])

  function onRiddleCategoryClick(nextRiddle?: number) {
    if (nextRiddle) {
      navigate(`/riddleok/${nextRiddle}`)
    } else {
      toast({
        title: 'Mindet megcsináltad!',
        description: 'Ebben a kategóriában nincs több riddle!',
        status: 'success',
        duration: 9000,
        isClosable: true
      })
    }
  }

  function progressGradient(progress: number, color: string) {
    const endDeg = 360 * progress
    if (progress === 1) {
      return `conic-gradient(${color} 0deg, ${color} 360deg)`
    }
    if (progress === 0) {
      return `conic-gradient(grey 0deg, gray 360deg)`
    }
    return `conic-gradient(grey 0deg,${color} 10deg, ${color} ${endDeg}deg, grey ${endDeg + 10}deg)`
  }

  if (loading) return <Loading />

  return (
    <Page {...props} loginRequired>
      <Helmet title="Riddleök" />
      <Heading>Riddleök</Heading>
      <VStack spacing={4} mt={5} align="stretch">
        {riddleCategoryList.map((riddleCategory) => (
          <Box
            bg={bg}
            px={6}
            py={2}
            borderRadius="md"
            _hover={{ bgColor: useColorModeValue('brand.300', 'brand.700'), cursor: 'pointer' }}
            onClick={() => onRiddleCategoryClick(riddleCategory.nextRiddle)}
          >
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
          </Box>
        ))}
      </VStack>
    </Page>
  )
}
