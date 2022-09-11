import { CmschPage } from '../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet-async'
import { useForm } from 'react-hook-form'
import { CreateTeamDto, TeamResponseMessages, TeamResponses } from '../../util/views/team.view'
import { Button, FormControl, FormLabel, Heading, Input } from '@chakra-ui/react'
import { useTeamCreate } from '../../api/hooks/team/useTeamCreate'
import { useNavigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'

export default function CreateTeamPage() {
  const navigate = useNavigate()
  const { register, handleSubmit, setError } = useForm<CreateTeamDto>()
  const { createTeamLoading, createTeamError, createTeam } = useTeamCreate((response) => {
    if (response === TeamResponses.OK) {
      navigate(AbsolutePaths.TEAMS + '/my')
    } else {
      setError('name', { message: TeamResponseMessages[response] })
    }
  })
  return (
    <CmschPage>
      <Helmet title="Csapat létrehozása" />
      <Heading>Csapat létrehozása</Heading>
      <form onSubmit={handleSubmit(createTeam)}>
        <FormControl my={10}>
          <FormLabel>Név</FormLabel>
          <Input {...register('name', { required: true })} placeholder="Kedves csapatom" />
        </FormControl>
        <Button type="submit" colorScheme="brand">
          Létrehozom!
        </Button>
      </form>
    </CmschPage>
  )
}
