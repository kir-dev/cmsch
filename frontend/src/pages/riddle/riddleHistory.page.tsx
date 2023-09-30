import { Box, Button, Center, Heading, HStack, Image, Select, Stack, Text, useToast, VStack } from '@chakra-ui/react'
import { useEffect, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import { useNavigate } from 'react-router-dom'
import { useRiddleHistoryQuery } from '../../api/hooks/riddle/useRiddleHistoryQuery'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { Loading } from '../../common-components/Loading'
import Markdown from '../../common-components/Markdown'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { SpoilerText } from './components/SpoilerText'
import { RiddleCategoryHistory } from '../../util/views/riddle.view'

const RiddleHistoryPage = () => {
  const toast = useToast()
  const navigate = useNavigate()
  const onError = () => toast({ title: l('riddle-history-query-failed'), status: 'error' })
  const [category, setCategory] = useState('')
  const [loaded, setLoaded] = useState(false)
  const [index, setIndex] = useState(0)
  const data = [
    {
      categoryName: 'Incisimogató \uD83D\uDC78\uD83E\uDD84\uD83C\uDF08',

      submissions: [
        {
          imageUrl: 'riddles/0000018a-acb5-3cf3-19c5-3d8d96a86384.png',
          title: 'Haladás',
          hint: '',
          solved: true,
          skipped: false,
          creator: 'Inci',
          firstSolver: 'SchöRétes Rend',
          solution: 'tejút',
          description: ''
        },
        {
          imageUrl: 'riddles/0000018a-acb6-690b-3fa9-58ef0e175652.png',
          title: 'Csapatmunka',
          hint: '',
          solved: true,
          skipped: false,
          creator: 'Inci',
          firstSolver: 'Egzisztenciálisch Krízisch',
          solution: 'Schugárút',
          description: ''
        },
        {
          imageUrl: 'riddles/0000018a-acb7-9d98-57c1-2b8c8ac24df9.jpg',
          title: 'Pajtások',
          hint: '',
          solved: true,
          skipped: false,
          creator: 'Inci',
          firstSolver: 'Egzisztenciálisch Krízisch',
          solution: 'Tom és Jerry',
          description: ''
        }
      ]
    }
  ]
  const query = {
    data: data,
    isSuccess: true,
    isError: false,
    isLoading: false
  }
  //const query = useRiddleHistoryQuery(onError)
  // useEffect(() => {
  //   if (!loaded && query.isSuccess) {
  //     setLoaded(true)
  //     if (query.data.length > 0) {
  //       setCategory(query.data!![0].categoryName)
  //     }
  //   }
  // }, [query.isSuccess])

  useEffect(() => {
    setIndex(0)
  }, [category])

  if (query.isError) {
    onError()
    navigate(AbsolutePaths.RIDDLE)
  }
  if (query.isLoading || !query.data) {
    return <Loading />
  }
  const riddleList = query.data.find((c) => c.categoryName === category)?.submissions
  const riddle = riddleList ? riddleList[index] : undefined
  const breadcrumbItems = [
    {
      title: 'Riddle',
      to: AbsolutePaths.RIDDLE
    },
    {
      title: 'Megoldott riddleök'
    }
  ]

  return (
    <CmschPage>
      <Helmet title="Megoldott riddleök" />
      <CustomBreadcrumb items={breadcrumbItems} />
      <Stack direction={['column', 'row']} justify="space-between" align={['flex-start', 'center']}>
        <Heading my={5}>Megoldott riddleök</Heading>
        <Select value={category} onChange={(e) => setCategory(e.target.value)} w="20rem">
          {query.data!!.map((c) => (
            <option value={c.categoryName} key={c.categoryName}>
              {c.categoryName} ({c.submissions.length} megoldott riddle)
            </option>
          ))}
        </Select>
        <Select value={riddleList ? riddleList[index].title : ''} onChange={(e) => setIndex(1)} w="20rem">
          {riddleList?.map((r, idx) => (
            <option value={r.title} key={idx}>
              {r.title}
            </option>
          ))}
        </Select>
      </Stack>

      {!riddle || !riddleList ? (
        <>
          <Text mt={2}>Ebben a kategóriában még nincsenek megoldott riddleök.</Text>
          <Center mt={3}>
            <LinkButton colorScheme="brand" href={AbsolutePaths.RIDDLE}>
              Összes riddle
            </LinkButton>
          </Center>
        </>
      ) : (
        <>
          <Box maxW="100%" w="30rem" mx="auto">
            <Heading my={5} size="lg">
              {riddle.title}
            </Heading>
            {riddle.imageUrl && <Image width="100%" src={`${API_BASE_URL}/cdn/${riddle.imageUrl}`} alt="Riddle Kép" borderRadius="md" />}

            <VStack mt={5} align="flex-start">
              <Text>Sorszám: {index + 1}</Text>
              <Text>Létrehozó: {riddle.creator || 'Nincs megadva'}</Text>
              <Text>Első megoldó: {riddle.firstSolver || 'Nincs megadva'}</Text>
              {riddle.description && <Markdown text={riddle.description} />}
              <Text>
                Megoldás (kattintásra jelenik meg): <SpoilerText text={riddle.solution} />
              </Text>
              <Text>
                Hint{riddle.hint && ' (kattintásra jelenik meg)'}:{' '}
                {riddle.hint ? <SpoilerText text={riddle.hint} /> : 'Nem lett felhasználva'}
              </Text>
            </VStack>

            <HStack justify="space-between" my={3}>
              <Button leftIcon={<FaArrowLeft />} onClick={() => setIndex(index - 1)} isDisabled={index === 0}>
                Előző
              </Button>
              <Button rightIcon={<FaArrowRight />} onClick={() => setIndex(index + 1)} isDisabled={index === riddleList?.length - 1}>
                Következő
              </Button>
            </HStack>
            <Center>
              <LinkButton colorScheme="brand" href={AbsolutePaths.RIDDLE}>
                Összes riddle
              </LinkButton>
            </Center>
          </Box>
        </>
      )}
    </CmschPage>
  )
}

export default RiddleHistoryPage
