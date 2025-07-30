workspace "SGT Infrastructure" {
    model {
        ss = softwareSystem "SGT" "SGT Infrastructure" {
            aksclusternode = container "AKS Cluster Node" "" "" "Kubernetes - node"
            k8singress = container "Kubernetes Ingress" "" "" "Kubernetes - ing"

            k8singress -> aksclusternode "Route incomming cluster traffic"

            frontend = container "SGT_frontend" "" "" "Kubernetes - pod"
            backoffice = container "SGT_backend" "" "" "Kubernetes - pod"
            database = container "SGT_database" "" "" "Kubernetes - pod"
        }
        development = deploymentEnvironment "Development" {
            deploymentNode "Subscription" {
                publicadordev = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n  - be-sgt.dev.ic.ama.lan - sgt.dev.ic.ama.lan\n \nRedirects -> \n - ingress.dev.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Development - DEV - Environment" {
                sncicddev = infrastructureNode "spk-DevOps-CICD-route-subnet-dev" "" "" "Microsoft Azure - Route Tables"
                k8singressdev = containerInstance k8singress {
                    description "ingress.dev.ic.ama.lan"
                }
                deploymentNode "AKS Cluster DEV" "" "" "Kubernetes - node" {
                    aksclusternodedev = containerInstance aksclusternode
                    deploymentNode "dev-sgt Namespace" "" "" "Kubernetes - ns" {
                        containerInstance frontend "" "largetext" {
                            description "sgt.dev.ic.ama.lan"
                        }
                        containerInstance backoffice "" "largetext" {
                            description "be-sgt.dev.ic.ama.lan"
                        }
                    }
                    deploymentNode "dev-sgt-db Namespace" "" "" "Kubernetes - ns" {
                        containerInstance database "" "largetext" {
                            description "db-postgresql-service.dev-sgt-db.srv.cluster.local"
                        }
                    }
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadordev -> k8singressdev "Forwards requests to ingress.dev.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
        test = deploymentEnvironment "Test" {
            deploymentNode "Subscription" {
                publicadortst = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n - be-sgt.tst.ic.ama.lan - sgt.tst.ic.ama.lan\n \nRedirects -> \n - ingress.tst.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Test - TST - Environment" {
                sncicdtst = infrastructureNode "spk-DevOps-CICD-route-subnet-tst" "" "" "Microsoft Azure - Route Tables"
                k8singresstst = containerInstance k8singress {
                    description "ingress.tst.ic.ama.lan"
                }
                deploymentNode "AKS Cluster TST" "" "" "Kubernetes - node" {
                    aksclusternodetst = containerInstance aksclusternode
                    deploymentNode "tst-sgt Namespace" "" "" "Kubernetes - ns" {
                        containerInstance frontend "" "largetext" {
                            description "sgt.tst.ic.ama.lan"
                        }
                        containerInstance backoffice "" "largetext" {
                            description "be-sgt.tst.ic.ama.lan"
                        }
                    }
                    deploymentNode "tst-sgt-db Namespace" "" "" "Kubernetes - ns" {
                        containerInstance database "" "largetext" {
                            description "db-postgresql-service.tst-sgt-db.srv.cluster.local"
                        }
                    }
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadortst -> k8singresstst "Forwards requests to ingress.tst.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
        preproduction = deploymentEnvironment "Pre-Production" {
            deploymentNode "Subscription" {
                publicadorppr = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n - be-sgt.ppr.ic.ama.lan - sgt.ppr.ic.ama.lan\n \nRedirects -> \n - ingress.ppr.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Pre-Production - PPR - Environment" {
                sncicdppr = infrastructureNode "spk-DevOps-CICD-route-subnet-ppr" "" "" "Microsoft Azure - Route Tables"
                k8singressppr = containerInstance k8singress {
                    description "ingress.ppr.ic.ama.lan"
                }
                deploymentNode "AKS Cluster PPR" "" "" "Kubernetes - node" {
                    aksclusternodeppr = containerInstance aksclusternode
                    deploymentNode "ppr-sgt Namespace" "" "" "Kubernetes - ns" {
                        containerInstance frontend "" "largetext" {
                            description "sgt.ppr.ic.ama.lan"
                        }
                        containerInstance backoffice "" "largetext" {
                            description "be-sgt.ppr.ic.ama.lan"
                        }
                    }
                    deploymentNode "ppr-sgt-db Namespace" "" "" "Kubernetes - ns" {
                        containerInstance database "" "largetext" {
                            description "db-postgresql-service.ppr-sgt-db.srv.cluster.local"
                        }
                    }
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadorppr -> k8singressppr "Forwards requests to ingress.ppr.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
        production = deploymentEnvironment "Production" {
            deploymentNode "Subscription" {
                publicadorprd = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n - be-sgt.prd.ic.ama.lan\n - sgt.prd.ic.ama.lan\n \nRedirects -> \n - ingress.prd.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Production - PRD - Environment" {
                sncicdprd = infrastructureNode "spk-DevOps-CICD-route-subnet-prd" "" "" "Microsoft Azure - Route Tables"
                k8singressprd = containerInstance k8singress {
                    description "ingress.prd.ic.ama.lan"
                }
                deploymentNode "AKS Cluster PRD" "" "" "Kubernetes - node" {
                    aksclusternodeprd = containerInstance aksclusternode
                    deploymentNode "pdr-sgt Namespace" "" "" "Kubernetes - ns" {
                        containerInstance frontend "" "largetext" {
                            description "sgt.pdr.ic.ama.lan"
                        }
                        containerInstance backoffice "" "largetext" {
                            description "be-sgt.pdr.ic.ama.lan"
                        }
                    }
                    deploymentNode "prd-sgt-db Namespace" "" "" "Kubernetes - ns" {
                        containerInstance database "" "largetext" {
                            description "db-postgresql-service.prd-sgt-db.srv.cluster.local"
                        }
                    }
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadorprd -> k8singressprd "Forwards requests to ingress.prd.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
    }

    views {
        deployment ss development {
            title "SGT - Development Environment"
            description "Default Kubernetes Cloud DEV environment"
            include *
            autoLayout tb
        }
        deployment ss test {
            title "SGT - Test Environment"
            description "Default Kubernetes Cloud TST environment"
            include *
            autoLayout tb
        }
        deployment ss preproduction {
            title "SGT - Pre-Production Environment"
            description "Default Kubernetes Cloud PPR environment"
            include *
            autoLayout tb
        }
        deployment ss production {
            title "SGT - Production Environment"
            description "Default Kubernetes Cloud PRD environment"
            include *
            autoLayout tb
        }
        themes https://static.structurizr.com/themes/microsoft-azure-2023.01.24/theme.json https://static.structurizr.com/themes/kubernetes-v0.3/theme.json
        styles {
            relationship "continuousarrow" {
                dashed false
                color #808000
            }
            relationship "continuousarrowfromoutside" {
                dashed false
                color #1966FF
            }
            element "filler" {
                width 1
                height 1
                description false
                metadata false
                opacity 100
            }
            element "Deployment Node" {
                metadata true
            }
            element "largetext" {
                width 900
                height 500
            }
        }
    }
}