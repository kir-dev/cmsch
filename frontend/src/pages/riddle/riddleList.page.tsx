import { Button, Heading, Stack, Text, useToast, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Link, useNavigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useRiddleListQuery } from '../../api/hooks/riddle/useRiddleListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { RiddleCategory } from '../../util/views/riddle.view.ts'
import { RiddleCategoryListItem } from './components/RiddleCategoryListItem'

const RiddleCategoryList = () => {
  const navigate = useNavigate()
  const toast = useToast()
  const component = useConfigContext()?.components?.riddle
  const { isLoading, isError, data } = useRiddleListQuery()
  const brandColor = useBrandColor()

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const onRiddleCategoryClick = (category: RiddleCategory) => {
    if (category.nextRiddles.length > 1) {
      navigate(`${AbsolutePaths.RIDDLE}/category/${category.categoryId}`)
    } else if (category.nextRiddles[0]) {
      navigate(`${AbsolutePaths.RIDDLE}/solve/${category.nextRiddles[0].id}`)
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

  return (
    <CmschPage>
      <Helmet title="Riddleök" />
      <Stack direction={['column', 'row']} justify="space-between" align={['flex-start', 'flex-end']}>
        <Heading as="h1" variant="main-title">
          Riddleök
        </Heading>
        <Button colorScheme={brandColor} as={Link} to={AbsolutePaths.RIDDLE_HISTORY}>
          Megoldott riddleök
        </Button>
      </Stack>
      <VStack spacing={4} mt={5} align="stretch">
        {(data ?? []).length > 0 ? (
          data.map((riddleCategory) => (
            <RiddleCategoryListItem
              category={riddleCategory}
              key={riddleCategory.categoryId}
              onClick={() => onRiddleCategoryClick(riddleCategory)}
            />
          ))
        ) : (
          <Text>Nincs egyetlen riddle feladat sem.</Text>
        )}
      </VStack>
    </CmschPage>
  )
}

export default RiddleCategoryList
