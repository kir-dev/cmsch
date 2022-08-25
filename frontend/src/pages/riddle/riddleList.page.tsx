import { Box, Flex, Heading, Text, useColorModeValue, useToast, VStack } from '@chakra-ui/react'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { useNavigate } from 'react-router-dom'
import { RiddleCategory } from '../../util/views/riddle.view'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { AbsolutePaths, Paths } from '../../util/paths'
import { l } from '../../util/language'

function progress(riddleCategory: RiddleCategory) {
  if (riddleCategory.completed === 0) {
    return 0
  }
  return riddleCategory.completed / riddleCategory.total
}

const RiddleCategoryList = () => {
  const bg = useColorModeValue('gray.200', 'gray.600')
  const navigate = useNavigate()
  const toast = useToast()

  const [riddleCategoryList, setRiddleCategoryList] = useState<RiddleCategory[]>([])
  const [loading, setLoading] = useState<boolean>(false)

  useEffect(() => {
    setLoading(true)
    axios
      .get<RiddleCategory[]>(`/api/${Paths.RIDDLE}`)
      .then((res) => {
        setRiddleCategoryList(res.data)
        setLoading(false)
      })
      .catch(() => {
        console.error('Nem sikerült lekérni a Riddle-öket.')
      })
  }, [setRiddleCategoryList])

  const onRiddleCategoryClick = (nextRiddle?: number) => {
    if (nextRiddle) {
      navigate(`${AbsolutePaths.RIDDLE}/${nextRiddle}`)
    } else {
      toast({
        title: l('riddle-completed-category-title'),
        description: l('riddle-completed-category-description'),
        status: 'success',
        duration: 9000,
        isClosable: true
      })
    }
  }

  const progressGradient = (progress: number, color: string) => {
    const endDeg = 360 * progress
    if (progress === 1) {
      return `conic-gradient(${color} 0deg, ${color} 360deg)`
    }
    if (progress === 0) {
      return `conic-gradient(white 0deg, white 360deg)`
    }
    return `conic-gradient(white 0deg,${color} 10deg, ${color} ${endDeg}deg, white ${endDeg + 10}deg)`
  }

  const riddleHoverBgColor = useColorModeValue('brand.300', 'brand.700')

  if (loading) return <Loading />

  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title="Riddleök" />
      <Heading>Riddleök</Heading>
      <VStack spacing={4} mt={5} align="stretch">
        {riddleCategoryList.length > 0 ? (
          <>
            {riddleCategoryList.map((riddleCategory) => (
              <Box
                key={riddleCategory.categoryId}
                bg={bg}
                px={6}
                py={2}
                borderRadius="md"
                _hover={{ bgColor: riddleHoverBgColor, cursor: 'pointer' }}
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
          </>
        ) : (
          <Text>Nincs egyetlen riddle feladat se.</Text>
        )}
      </VStack>
    </CmschPage>
  )
}

export default RiddleCategoryList
