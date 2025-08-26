import {useQuery} from "@tanstack/react-query";
import { OptionalTournamentView } from '../../../../util/views/tournament.view.ts'
import {QueryKeys} from "../../queryKeys.ts";
import axios from "axios";
import {joinPath} from "../../../../util/core-functions.util.ts";
import {ApiPaths} from "../../../../util/paths.ts";


export const useTournamentQuery = (id: number) => {
  return useQuery<OptionalTournamentView, Error>({
    queryKey: [QueryKeys.TOURNAMENTS, id],
    queryFn: async () => {
      const response = await axios.get<OptionalTournamentView>(joinPath(ApiPaths.TOURNAMENTS, id))
      return response.data
    }
  })
}
