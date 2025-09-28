import { Button, Heading, Stack, Text, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Link, useNavigate, useParams } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useRiddleListQuery } from '../../api/hooks/riddle/useRiddleListQuery.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { AbsolutePaths } from '../../util/paths'
import { RiddleListItem } from './components/RiddleListItem.tsx'

const RiddleCategoryPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const component = useConfigContext()?.components?.riddle
  const { isLoading, isError, data } = useRiddleListQuery()

  if (!component) return <ComponentUnavailable />

  const category = data?.find((category) => category.categoryId === Number(id))
  if (isError || isLoading || !category) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const riddles = category.nextRiddles
  const breadcrumbItems = [
    {
      title: 'Riddle',
      to: AbsolutePaths.RIDDLE
    },
    {
      title: category.title
    }
  ]
  return (
    <CmschPage>
      <Helmet title="Riddleök" />
      <CustomBreadcrumb items={breadcrumbItems} />
      <Stack direction={['column', 'row']} justify="space-between" align={['flex-start', 'flex-end']}>
        <Heading as="h1" variant="main-title">
          Riddleök | {category.title}
        </Heading>
        <Button colorScheme="brand" as={Link} to={AbsolutePaths.RIDDLE_HISTORY}>
          Megoldott riddleök
        </Button>
      </Stack>
      <VStack spacing={4} mt={5} align="stretch">
        {(riddles ?? []).length > 0 ? (
          riddles.map((riddle) => (
            <RiddleListItem riddle={riddle} key={riddle.id} onClick={() => navigate(`${AbsolutePaths.RIDDLE}/solve/${riddle.id}`)} />
          ))
        ) : (
          <Text>Nincs egyetlen megoldásra váró riddle feladat sem. Szép munka!</Text>
        )}
      </VStack>
    </CmschPage>
  )
}

export default RiddleCategoryPage
