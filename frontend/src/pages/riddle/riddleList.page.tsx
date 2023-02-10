import { Box, Flex, Heading, Text, useColorModeValue, useToast, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate } from 'react-router-dom'
import { RiddleCategory } from '../../util/views/riddle.view'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'
import { useRiddleListQuery } from '../../api/hooks/useRiddleListQuery'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { LoadingPage } from '../loading/loading.page'

function progress(riddleCategory: RiddleCategory) {
  if (riddleCategory.completed === 0) {
    return 0
  }
  return riddleCategory.completed / riddleCategory.total
}

const RiddleCategoryList = () => {
  const navigate = useNavigate()
  const { sendMessage } = useServiceContext()
  const toast = useToast()

  const queryResult = useRiddleListQuery(() => toast({ title: l('riddle-query-failed'), status: 'error' }))

  if (queryResult.isError) {
    sendMessage(l('riddle-query-failed') + queryResult.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (queryResult.isLoading) {
    return <LoadingPage />
  }

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

  const bg = useColorModeValue('brand.100', 'brand.500')
  const hoverBg = useColorModeValue('brand.200', 'brand.400')

  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title="Riddleök" />
      <Heading>Riddleök</Heading>
      <VStack spacing={4} mt={5} align="stretch">
        {(queryResult.data || []).length > 0 ? (
          <>
            {queryResult.data?.map((riddleCategory) => (
              <Box
                key={riddleCategory.categoryId}
                bg={bg}
                px={6}
                py={2}
                borderRadius="md"
                _hover={{ bgColor: hoverBg, cursor: 'pointer' }}
                onClick={() => onRiddleCategoryClick(riddleCategory.nextRiddle)}
              >
                <Flex align="center" justifyContent="space-between">
                  <Text fontWeight="bold" fontSize="xl">
                    {riddleCategory.title}
                  </Text>
                  <Box bgGradient={progressGradient(progress(riddleCategory), 'green.400')} px={1} py={1} borderRadius="6px">
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
